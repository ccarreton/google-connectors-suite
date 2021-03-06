/**
 * Mule Google Api Commons
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package com.google.gdata.data.extensions;

import com.google.gdata.data.AbstractExtension;
import com.google.gdata.data.AttributeGenerator;
import com.google.gdata.data.AttributeHelper;
import com.google.gdata.data.ExtensionDescription;
import com.google.gdata.util.Namespaces;
import com.google.gdata.util.ParseException;

/**
 * Name of organization.
 *
 * 
 */
@ExtensionDescription.Default(
    nsAlias = Namespaces.gAlias,
    nsUri = Namespaces.g,
    localName = OrgName.XML_NAME)
public class OrgName extends AbstractExtension {

  /** XML element name */
  static final String XML_NAME = "orgName";

  /** XML "yomi" attribute name */
  private static final String YOMI = "yomi";

  /** Value */
  private String value = null;

  /** Yomi name of organization */
  private String yomi = null;

  /**
   * Default mutable constructor.
   */
  public OrgName() {
    super();
  }

  /**
   * Immutable constructor.
   *
   * @param value value.
   * @param yomi yomi name of organization.
   */
  public OrgName(String value, String yomi) {
    super();
    setValue(value);
    setYomi(yomi);
    setImmutable(true);
  }

  /**
   * Returns the value.
   *
   * @return value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value value or <code>null</code> to reset
   */
  public void setValue(String value) {
    throwExceptionIfImmutable();
    this.value = value;
  }

  /**
   * Returns whether it has the value.
   *
   * @return whether it has the value
   */
  public boolean hasValue() {
    return getValue() != null;
  }

  /**
   * Returns the yomi name of organization.
   *
   * @return yomi name of organization
   */
  public String getYomi() {
    return yomi;
  }

  /**
   * Sets the yomi name of organization.
   *
   * @param yomi yomi name of organization or <code>null</code> to reset
   */
  public void setYomi(String yomi) {
    throwExceptionIfImmutable();
    this.yomi = yomi;
  }

  /**
   * Returns whether it has the yomi name of organization.
   *
   * @return whether it has the yomi name of organization
   */
  public boolean hasYomi() {
    return getYomi() != null;
  }

  @Override
  protected void validate() {
  }

  /**
   * Returns the extension description, specifying whether it is required, and
   * whether it is repeatable.
   *
   * @param required   whether it is required
   * @param repeatable whether it is repeatable
   * @return extension description
   */
  public static ExtensionDescription getDefaultDescription(boolean required,
      boolean repeatable) {
    ExtensionDescription desc =
        ExtensionDescription.getDefaultDescription(OrgName.class);
    desc.setRequired(required);
    desc.setRepeatable(repeatable);
    return desc;
  }

  @Override
  protected void putAttributes(AttributeGenerator generator) {
    generator.setContent(value);
    generator.put(YOMI, yomi);
  }

  @Override
  protected void consumeAttributes(AttributeHelper helper) throws ParseException
      {
    value = helper.consume(null, false);
    yomi = helper.consume(YOMI, false);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!sameClassAs(obj)) {
      return false;
    }
    OrgName other = (OrgName) obj;
    return eq(value, other.value)
        && eq(yomi, other.yomi);
  }

  @Override
  public int hashCode() {
    int result = getClass().hashCode();
    if (value != null) {
      result = 37 * result + value.hashCode();
    }
    if (yomi != null) {
      result = 37 * result + yomi.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return "{OrgName value=" + value + " yomi=" + yomi + "}";
  }


  /**
   * Immutable constructor for backward compatibility.
   *
   * @param value value.
   */
  public OrgName(String value) {
    super();
    setValue(value);
    setImmutable(true);
  }
}
