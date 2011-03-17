/**
 * File: SerializationParser.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Sep 13, 2010
 * Creation Time: 3:37:35 PM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package org.localmatters.serializer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.localmatters.serializer.serialization.Serialization;


public interface SerializationParser {
    Map<String, Serialization> parse(InputStream input) throws IOException;
}
