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
package com.localmatters.serializer.config.parser;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.Resource;

import com.localmatters.serializer.serialization.Serialization;

public interface SerializationParser {
    Map<String, Serialization> parse(Resource input) throws IOException;
}
