/**
 * Mule Google Api Commons
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package com.google.gdata.data;

/**
 * Shared person interface.
 * 
 * 
 */
public interface IPerson {

  /** Human-readable name. */
  public String getName();

  /** Language of name */
  public String getNameLang();

  /** Email address. */
  public String getEmail();

  /** Uri associated with the person */
  public String getUri();
}
