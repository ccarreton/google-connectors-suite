/**
 * Mule Google Api Commons
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package com.google.gdata.wireformats.input;

import com.google.gdata.wireformats.StreamPropertiesBuilder;


/**
 * The InputPropertiesBuilder class is a builder class that aids in the
 * construction of new {@link InputProperties} instances.
 * 
 * 
 */
public class InputPropertiesBuilder 
    extends StreamPropertiesBuilder<InputPropertiesBuilder> {

  private Class<?> expectType;

  /**
   * Constructs a new InputPropertiesBuilder with no properties set.
   */
  public InputPropertiesBuilder() {
    super();
  }

  /**
   * Constructs a new InputPropertiesBuilder with properties set from an
   * existing {@link InputProperties} instance.
   * 
   * @param source input properties instance to copy from
   */
  public InputPropertiesBuilder(InputProperties source) {
    super(source);
    this.expectType = source.getRootType();
  }

  /**
   * Sets the expected type property that should be used for instances
   * created by the builder.
   * 
   * @param expectType expectType to set in built instances.
   * @return this builder (to enable initialization chaining).
   */
  public InputPropertiesBuilder setExpectType(Class<?> expectType) {
    this.expectType = expectType;
    return this;
  }

  /**
   * Returns a new {@link InputProperties} instance initialized with the
   * property values set on the builder.
   */
  public InputProperties build() {
    return new InputPropertiesImpl(this);
  }
  /**
   * The InputPropertiesImpl class is a simple immutable value object that
   * implements the {@link InputProperties} interface.
   */
  private static class InputPropertiesImpl extends StreamPropertiesImpl 
      implements InputProperties {

    private final Class<?> expectType;
    
    /**
     * Constructs a new InputPropertiesImpl instance with the property values
     * from a builder instance.
     * 
     * @param builder instance.
     */
    private InputPropertiesImpl(InputPropertiesBuilder builder) {
      super(builder);
      this.expectType = builder.expectType;
    }

    public Class<?> getRootType() {
      return expectType;
    }
  }
}
