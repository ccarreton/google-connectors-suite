/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.google.code.com.sun.mail.imap.protocol;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

import com.google.code.com.sun.mail.iap.Argument;
import com.google.code.com.sun.mail.iap.ProtocolException;
import com.google.code.com.sun.mail.iap.Response;
import com.google.code.com.sun.mail.util.ASCIIUtility;
import com.google.code.com.sun.mail.util.BASE64DecoderStream;
import com.google.code.com.sun.mail.util.BASE64EncoderStream;
import com.google.code.com.sun.mail.util.PropUtil;

/**
 * This class contains a single method that does authentication using
 * SASL.  This is in a separate class so that it can be compiled with
 * J2SE 1.5.  Eventually it should be merged into IMAPProtocol.java.
 */

public class IMAPSaslAuthenticator implements SaslAuthenticator {

    private IMAPProtocol pr;
    private String name;
    private Properties props;
    private boolean debug;
    private PrintStream out;
    private String host;

    public IMAPSaslAuthenticator(IMAPProtocol pr, String name, Properties props,
				boolean debug, PrintStream out, String host) {
	this.pr = pr;
	this.name = name;
	this.props = props;
	this.debug = debug;
	this.out = out;
	this.host = host;
    }

    public boolean authenticate(String[] mechs, final String realm,
				final String authzid, final String u,
				final String p) throws ProtocolException {

	synchronized (pr) {	// authenticate method should be synchronized
	Vector v = new Vector();
	String tag = null;
	Response r = null;
	boolean done = false;
	if (debug) {
	    out.print("IMAP SASL DEBUG: Mechanisms:");
	    for (int i = 0; i < mechs.length; i++)
		out.print(" " + mechs[i]);
	    out.println();
	}

	SaslClient sc;
	CallbackHandler cbh = new CallbackHandler() {
	    public void handle(Callback[] callbacks) {
		if (debug)
		    out.println("IMAP SASL DEBUG: callback length: " +
							callbacks.length);
		for (int i = 0; i < callbacks.length; i++) {
		    if (debug)
			out.println("IMAP SASL DEBUG: callback " + i + ": " +
							callbacks[i]);
		    if (callbacks[i] instanceof NameCallback) {
			NameCallback ncb = (NameCallback)callbacks[i];
			ncb.setName(u);
		    } else if (callbacks[i] instanceof PasswordCallback) {
			PasswordCallback pcb = (PasswordCallback)callbacks[i];
			pcb.setPassword(p.toCharArray());
		    } else if (callbacks[i] instanceof RealmCallback) {
			RealmCallback rcb = (RealmCallback)callbacks[i];
			rcb.setText(realm != null ?
				    realm : rcb.getDefaultText());
		    } else if (callbacks[i] instanceof RealmChoiceCallback) {
			RealmChoiceCallback rcb =
			    (RealmChoiceCallback)callbacks[i];
			if (realm == null)
			    rcb.setSelectedIndex(rcb.getDefaultChoice());
			else {
			    // need to find specified realm in list
			    String[] choices = rcb.getChoices();
			    for (int k = 0; k < choices.length; k++) {
				if (choices[k].equals(realm)) {
				    rcb.setSelectedIndex(k);
				    break;
				}
			    }
			}
		    }
		}
	    }
	};

	try {
	    sc = Sasl.createSaslClient(mechs, authzid, name, host,
					(Map)props, cbh);
	} catch (SaslException sex) {
	    if (debug)
		out.println("IMAP SASL DEBUG: Failed to create SASL client: " +
								sex);
	    return false;
	}
	if (sc == null) {
	    if (debug)
		out.println("IMAP SASL DEBUG: No SASL support");
	    return false;
	}
	if (debug)
	    out.println("IMAP SASL DEBUG: SASL client " +
						sc.getMechanismName());

	try {
	    tag = pr.writeCommand("AUTHENTICATE " + sc.getMechanismName(),
						null);
	} catch (Exception ex) {
	    if (debug)
		out.println("IMAP SASL DEBUG: AUTHENTICATE Exception: " + ex);
	    return false;
	}

	OutputStream os = pr.getIMAPOutputStream(); // stream to IMAP server

	/*
	 * Wrap a BASE64Encoder around a ByteArrayOutputstream
	 * to craft b64 encoded username and password strings
	 *
	 * Note that the encoded bytes should be sent "as-is" to the
	 * server, *not* as literals or quoted-strings.
	 *
	 * Also note that unlike the B64 definition in MIME, CRLFs 
	 * should *not* be inserted during the encoding process. So, I
	 * use Integer.MAX_VALUE (0x7fffffff (> 1G)) as the bytesPerLine,
	 * which should be sufficiently large !
	 */

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	byte[] CRLF = { (byte)'\r', (byte)'\n'};

	// Hack for Novell GroupWise XGWTRUSTEDAPP authentication mechanism
	boolean isXGWTRUSTEDAPP =
	    sc.getMechanismName().equals("XGWTRUSTEDAPP") &&
	    PropUtil.getBooleanProperty(props,
		"mail." + name + ".sasl.xgwtrustedapphack.enable", true);
	while (!done) { // loop till we are done
	    try {
		r = pr.readResponse();
	    	if (r.isContinuation()) {
		    byte[] ba = null;
		    if (!sc.isComplete()) {
			ba = r.readByteArray().getNewBytes();
			if (ba.length > 0)
			    ba = BASE64DecoderStream.decode(ba);
			if (debug)
			    out.println("IMAP SASL DEBUG: challenge: " +
				ASCIIUtility.toString(ba, 0, ba.length) + " :");
			ba = sc.evaluateChallenge(ba);
		    }
		    if (ba == null) {
			if (debug)
			    out.println("IMAP SASL DEBUG: no response");
			os.write(CRLF); // write out empty line
			os.flush(); 	// flush the stream
			bos.reset(); 	// reset buffer
		    } else {
			if (debug)
			    out.println("IMAP SASL DEBUG: response: " +
				ASCIIUtility.toString(ba, 0, ba.length) + " :");
			ba = BASE64EncoderStream.encode(ba);
			if (isXGWTRUSTEDAPP)
			    bos.write("XGWTRUSTEDAPP ".getBytes());
			bos.write(ba);

			bos.write(CRLF); 	// CRLF termination
			os.write(bos.toByteArray()); // write out line
			os.flush(); 	// flush the stream
			bos.reset(); 	// reset buffer
		    }
		} else if (r.isTagged() && r.getTag().equals(tag))
		    // Ah, our tagged response
		    done = true;
		else if (r.isBYE()) // outta here
		    done = true;
		else // hmm .. unsolicited response here ?!
		    v.addElement(r);
	    } catch (Exception ioex) {
		if (debug)
		    ioex.printStackTrace();
		// convert this into a BYE response
		r = Response.byeResponse(ioex);
		done = true;
		// XXX - ultimately return true???
	    }
	}

	if (sc.isComplete() /*&& res.status == SUCCESS*/) {
	    String qop = (String)sc.getNegotiatedProperty(Sasl.QOP);
	    if (qop != null && (qop.equalsIgnoreCase("auth-int") ||
				qop.equalsIgnoreCase("auth-conf"))) {
		// XXX - NOT SUPPORTED!!!
		if (debug)
		    out.println("IMAP SASL DEBUG: " +
			"Mechanism requires integrity or confidentiality");
		return false;
	    }
	}

	/* Dispatch untagged responses.
	 * NOTE: in our current upper level IMAP classes, we add the
	 * responseHandler to the Protocol object only *after* the 
	 * connection has been authenticated. So, for now, the below
	 * code really ends up being just a no-op.
	 */
	Response[] responses = new Response[v.size()];
	v.copyInto(responses);
	pr.notifyResponseHandlers(responses);

	// Handle the final OK, NO, BAD or BYE response
	pr.handleResult(r);
	pr.setCapabilities(r);

	/*
	 * If we're using the Novell Groupwise XGWTRUSTEDAPP mechanism
	 * we always have to issue a LOGIN command to select the user
	 * we want to operate as.
	 */
	if (isXGWTRUSTEDAPP) {
	    Argument args = new Argument();
	    args.writeString(authzid != null ? authzid : u);

	    responses = pr.command("LOGIN", args);

	    // dispatch untagged responses
	    pr.notifyResponseHandlers(responses);

	    // Handle result of this command
	    pr.handleResult(responses[responses.length-1]);
	    // If the response includes a CAPABILITY response code, process it
	    pr.setCapabilities(responses[responses.length-1]);
	}
	return true;
    }
    }
}
