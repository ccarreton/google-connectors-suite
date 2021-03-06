/**
 * Mule Google Api Commons
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package com.google.gdata.data.blogger;

import com.google.gdata.data.BaseFeed;

/**
 * Describes a feed of an entire blog's comments.
 *
 * 
 */
public class BlogCommentFeed extends BaseFeed<BlogCommentFeed, CommentEntry> {

  /**
   * Default mutable constructor.
   */
  public BlogCommentFeed() {
    super(CommentEntry.class);
  }

  /**
   * Constructs a new instance by doing a shallow copy of data from an existing
   * {@link BaseFeed} instance.
   *
   * @param sourceFeed source feed
   */
  public BlogCommentFeed(BaseFeed<?, ?> sourceFeed) {
    super(CommentEntry.class, sourceFeed);
  }

  @Override
  public String toString() {
    return "{BlogCommentFeed " + super.toString() + "}";
  }

}
