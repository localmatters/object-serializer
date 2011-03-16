/**
 * File: XmlSerializationParser.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Sep 13, 2010
 * Creation Time: 3:38:59 PM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package com.localmatters.serializer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.localmatters.serializer.serialization.Serialization;

public class XmlSerializationParser implements SerializationParser {
    private String _defaultEncoding = "UTF-8";

    public String getDefaultEncoding() {
        return _defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        _defaultEncoding = defaultEncoding;
    }

    /**
     * @see com.localmatters.serializer.config.SerializationParser#parse(java.io.InputStream)
     */
    public Map<String, Serialization> parse(InputStream input) throws IOException {
        try {
            SerializationElementHandler handler = new SerializationElementHandler();

            SAXReader saxReader = new SAXReader();
            saxReader.addHandler("/" + SerializationElementHandler.TYPE_ROOT, handler);
            saxReader.setEncoding(getDefaultEncoding());
            saxReader.read(input);
            return handler.getSerializations();

        }
        catch (DocumentException e) {
            if (e.getNestedException() instanceof ConfigurationException) {
                throw (ConfigurationException) e.getNestedException();
            }
            throw new ConfigurationException("Unable to parse the serialization configuration!", e);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }
}
