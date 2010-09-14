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
package com.localmatters.serializer.config.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;

import com.localmatters.serializer.config.ConfigurationException;
import com.localmatters.serializer.config.SerializationElementHandler;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.util.objectfactory.LMObjectFactory;

public class XmlSerializationParser implements SerializationParser {
    private String _defaultEncoding = "UTF-8";
    private LMObjectFactory _objectFactory;

    public LMObjectFactory getObjectFactory() {
        return _objectFactory;
    }

    public void setObjectFactory(LMObjectFactory objectFactory) {
        _objectFactory = objectFactory;
    }

    public String getDefaultEncoding() {
        return _defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        _defaultEncoding = defaultEncoding;
    }

    public Map<String, Serialization> parse(Resource resource) throws IOException {
        InputStream input = null;
        try {
            input= resource.getInputStream();
            SerializationElementHandler handler = new SerializationElementHandler(getObjectFactory());

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
            throw new ConfigurationException(
                String.format("Unable to load the handlers configuration %s!", resource.getDescription()), e);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }
}
