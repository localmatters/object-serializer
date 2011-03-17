/*
   Copyright 2010-present Local Matters, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.localmatters.serializer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.localmatters.serializer.serialization.Serialization;


public class XmlSerializationParser implements SerializationParser {
    private String _defaultEncoding = "UTF-8";

    public String getDefaultEncoding() {
        return _defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        _defaultEncoding = defaultEncoding;
    }

    /**
     * @see org.localmatters.serializer.config.SerializationParser#parse(java.io.InputStream)
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
