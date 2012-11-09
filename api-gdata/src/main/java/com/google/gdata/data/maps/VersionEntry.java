/**
 * Mule Google Api Commons
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package com.google.gdata.data.maps;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Category;
import com.google.gdata.data.Kind;
import com.google.gdata.data.Link;
import com.google.gdata.util.Namespaces;

/**
 * Describes a version entry.
 *
 * 
 */
@Kind.Term(VersionEntry.KIND)
public class VersionEntry extends BaseEntry<VersionEntry> {

  /**
   * Version version category kind term value.
   */
  public static final String KIND = MapsNamespace.MAPS_PREFIX + "version";

  /**
   * Version version category kind category.
   */
  public static final Category CATEGORY = new Category(Namespaces.gKind, KIND);

  /**
   * Default mutable constructor.
   */
  public VersionEntry() {
    super();
    getCategories().add(CATEGORY);
  }

  /**
   * Constructs a new instance by doing a shallow copy of data from an existing
   * {@link BaseEntry} instance.
   *
   * @param sourceEntry source entry
   */
  public VersionEntry(BaseEntry<?> sourceEntry) {
    super(sourceEntry);
  }

  /**
   * Returns the link that provides the URI of the full feed (without any query
   * parameters).
   *
   * @return Link that provides the URI of the full feed (without any query
   *     parameters) or {@code null} for none.
   */
  public Link getAtomFeedLink() {
    return getLink(Link.Rel.FEED, Link.Type.ATOM);
  }

  @Override
  public String toString() {
    return "{VersionEntry " + super.toString() + "}";
  }

}

