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
package org.localmatters.serializer;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.localmatters.serializer.resolver.PropertyResolver;
import org.localmatters.serializer.writer.Writer;


/**
 * Provides the current context for the serialization.
 */
public class SerializationContext {
	private static final String SEPARATOR = ".";
	private Stack<String> levels;
	private Writer writer;
	private PropertyResolver resolver;
	private OutputStream outputStream;
	private Map<String, Object> beans;
	private boolean formatting = false;
	private Stack<byte[]> prefixes = new Stack<byte[]>();

	/**
	 * Constructor with the specification of the writer, the property resolver,
	 * the outputStream and the map of beans
	 * @param writer The writer
	 * @param resolver The resolver to resolve the property of an object
	 * @param os The outputStream where to write
	 * @param beans The map of beans available to the serialization
	 */
	public SerializationContext(Writer writer, PropertyResolver resolver, OutputStream os, Map<String, Object> beans) {
		setWriter(writer);
		setPropertyResolver(resolver);
		setOutputStream(os);
		setBeans(beans);
		levels = new Stack<String>();
	}

	/**
	 * Constructor with the specification of the writer, the property resolver
	 * and the outputStream
	 * @param writer The writer
	 * @param resolver The resolver to resolve the property of an object
	 * @param os The outputStream where to write
	 */
	public SerializationContext(Writer writer, PropertyResolver resolver, OutputStream os) {
		this(writer, resolver, os, new HashMap<String, Object>());
	}

	/**
	 * @return The deepness of this context
	 */
	public int getDeepness() {
		return CollectionUtils.size(levels);
	}
	
	/**
	 * Navigates to the next level
	 * @param level The next level 
	 * @return The serialization context for this level
	 */
	public SerializationContext nextLevel(String level) {
		if (levels.isEmpty()) {
			levels.push(level);
		} else {
			levels.push(SEPARATOR + level);
		}
		return this;
	}
	
	/**
	 * Descents to the previous level
	 */
	public void previousLevel() {
		levels.pop();
	}
	
	/**
	 * @return The path
	 */
	public String getPath() {
		StringBuilder sb = new StringBuilder();
		for (String level : levels) {
			sb.append(level);
		}
		return sb.toString();
	}
	
	/**
	 * @return The writer
	 */
	public Writer getWriter() {
		return writer;
	}
	
	/**
	 * @param writer The writer
	 */
	private void setWriter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Returns the bean exposed under the specified name
	 * @param name The name of the bean to retrieve
	 * @return The corresponding bean or null
	 */
	public Object getBean(String name) {
		return beans.get(name);
	}
	
	/**
	 * @param beans The map of beans available to the serialization
	 */
	private void setBeans(Map<String, Object> beans) {
		this.beans = beans;
	}
	
	/**
	 * @return The resolver to resolve the property of an object
	 */
	public PropertyResolver getPropertyResolver() {
		return resolver;
	}

	/**
	 * @param resolver The resolver to resolve the property of an object
	 */
	public void setPropertyResolver(PropertyResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * @return The outputStream where to write
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param os The outputStream where to write
	 */
	public void setOutputStream(OutputStream os) {
		this.outputStream = os;
	}

	/**
	 * @return Whether the output should be formatted or not
	 */
	public boolean isFormatting() {
		return formatting;
	}

	/**
	 * @param formatting Whether the output should be formatted or not
	 */
	public void setFormatting(boolean formatting) {
		this.formatting = formatting;
	}

	/**
	 * @return The prefix that should be written the next time a write occurs
	 */
	public byte[] consomePrefix() {
	    if (prefixes.isEmpty()) {
	        return null;
	    }
	    return prefixes.pop();
	}

	/**
	 * @param prefix The prefix that should be written the next time a write 
	 * occurs
	 */
	public void pushPrefix(byte[] prefix) {
		prefixes.push(prefix);
	}

    /**
     * @param prefix The prefix that should be written the next time a write 
     * occurs
     */
    public void pushPrefix(String prefix) {
        prefixes.push(prefix.getBytes());
    }
	
	/**
	 * @return The stack of prefixes
	 */
	public Stack<byte[]> getPrefixes() {
        return prefixes;
    }
}
