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
package org.localmatters.serializer.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;
import org.localmatters.serializer.util.EscapeUtils;
import org.localmatters.serializer.util.ReflectionUtils;


/**
 * This class defines a serialization writer that outputs JSON.
 */
public class JSONWriter extends AbstractWriter {
	private static final byte[] NULL = "null".getBytes();
	private static final byte[] LEFT_CURLY = "{".getBytes();
	private static final byte[] RIGHT_CURLY = "}".getBytes();
	private static final byte[] LEFT_SQUARE = "[".getBytes();
	private static final byte[] RIGHT_SQUARE = "]".getBytes();
	private static final byte[] QUOTE = "\"".getBytes();
	private static final byte[] QUOTE_COLUMN = "\": ".getBytes();
	private static final byte[] QUOTE_COLUMN_LEFT_CURLY = "\": {".getBytes();
	private static final byte[] QUOTE_COLUMN_CLOSED_CURLY = "\": {}".getBytes();
	private static final byte[] QUOTE_COLUMN_LEFT_SQUARE = "\": [".getBytes();
	private static final byte[] QUOTE_COLUMN_CLOSED_SQUARE = "\": []".getBytes();
	private static final byte[] COMMA = ", ".getBytes();
	private static final String INDENTATION = "   ";
	private Map<Integer, String> prefixes = new HashMap<Integer, String>();

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeRoot(org.localmatters.serializer.serialization.Serialization, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeRoot(Serialization ser, 
			Object root, 
			SerializationContext ctx) throws SerializationException {
		Serialization contextless = ser.getContextlessSerialization();
		if (contextless instanceof ValueSerialization) {
			write(ctx, LEFT_CURLY);
			ser.serialize(ser, null, root, ctx);
			write(ctx, getPrefix(ctx)).write(ctx, RIGHT_CURLY);
		} else if (ser instanceof NameSerialization) {
			contextless = ((NameSerialization) ser).getDelegate();
			contextless.serialize(contextless, null, root, ctx);
		} else {
			ser.serialize(ser, null, root, ctx);
		}
		
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeValue(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeValue(Serialization ser, 
			String name,
			Object value, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, "value"));

		String prefix = getPrefix(ctx);
		if (value != null) {
			write(ctx, prefix);
			if (StringUtils.isNotBlank(name)) {
				write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN);
			}

			String str = String.valueOf(value);
			if (ReflectionUtils.isNumeric(value.getClass()) || ReflectionUtils.isBoolean(value.getClass())) {
				write(ctx, str);
			} else {
				write(ctx, QUOTE).write(ctx, EscapeUtils.escapeJson(str)).write(ctx, QUOTE);
			}
		} else if (ser.isWriteEmpty()) {
			write(ctx, prefix);
			if (StringUtils.isNotBlank(name)) {
				write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN);
			}
			write(ctx, NULL);
		}

		ctx.previousLevel();
	}
	
	/**
	 * @see org.localmatters.serializer.writer.Writer#writeAttribute(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeAttribute(Serialization ser, 
			String name,
			Object attribute, 
			SerializationContext ctx) throws SerializationException {
		writeValue(ser, checkRequiredName(ctx, name), attribute, ctx);
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeComplex(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, java.util.Collection, java.util.Collection, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	public void writeComplex(Serialization ser, 
			String name,
			Object object, 
			Collection<Serialization> attributes, 
			Collection<Serialization> elements, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, "complex"));

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(attributes) || CollectionUtils.isNotEmpty(elements)) {
			if (StringUtils.isEmpty(name)) {
				write(ctx, prefix).write(ctx, LEFT_CURLY);
			} else {
				write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_CURLY);
			}
			byte[] sep = new byte[]{};
			
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Serialization attribute : attributes) {
					ctx.pushPrefix(sep);
					attribute.serialize(attribute, null, object, ctx);
					if (ctx.consomePrefix() == null) {
						sep = COMMA;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(elements)) {
				for (Serialization element : elements) {
					ctx.pushPrefix(sep);
					element.serialize(element, null, object, ctx);
					if (ctx.consomePrefix() == null) {
						sep = COMMA;
					}
				}
			}
			write(ctx, prefix).write(ctx, RIGHT_CURLY);
		} else if (ser.isWriteEmpty()){
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_CURLY);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeIterator(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Iterator, java.lang.String, org.localmatters.serializer.serialization.Serialization, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	public void writeIterator(Serialization ser, 
			String name,
			Iterator<?> itr, 
			String elementName,
			Serialization element, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, "iterator"));

		String prefix = getPrefix(ctx);
		if (itr.hasNext()) {
			if (StringUtils.isEmpty(name)) {
				write(ctx, prefix).write(ctx, LEFT_SQUARE);
			} else {
				write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_SQUARE);
			}
			byte[] sep = new byte[]{};
			while (itr.hasNext()) {
				ctx.pushPrefix(sep);
				element.serialize(element, null, itr.next(), ctx);
				if (ctx.consomePrefix() == null) {
					sep = COMMA;
				}
			}
			write(ctx, prefix).write(ctx, RIGHT_SQUARE);
		} else if (ser.isWriteEmpty()) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_SQUARE);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeMap(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Collection, java.lang.String, org.localmatters.serializer.serialization.Serialization, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	@SuppressWarnings("rawtypes")
	public void writeMap(Serialization ser, 
			String name,
			Collection<Map.Entry> entries, 
			String key,
			Serialization value, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, "map"));

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(entries)) {
			if (StringUtils.isEmpty(name)) {
				write(ctx, prefix).write(ctx, LEFT_CURLY);
			} else {
				write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_CURLY);
			}
			byte[] sep = new byte[]{};
			for (Map.Entry entry : entries) {
				ctx.pushPrefix(sep);
				value.serialize(value, resolvesMapKey(key, entry, ctx), entry.getValue(), ctx);
				if (ctx.consomePrefix() == null) {
					sep = COMMA;
				}
			}
			write(ctx, prefix).write(ctx, RIGHT_CURLY);
		} else if (ser.isWriteEmpty()) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_CURLY);
		}

		ctx.previousLevel();
	}

	/**
	 * Returns the prefix
	 * @param ctx The context
	 * @return The prefix
	 */
	protected String getPrefix(SerializationContext ctx) {
		String prefix = "";
		int deepness = ctx.getDeepness();
		if (ctx.isFormatting()) {
			prefix = prefixes.get(deepness);
			if (prefix == null) {
				prefix = "\n";
				for (int i=1; i<deepness; i++) {
					prefix += INDENTATION;
				}
				prefixes.put(deepness, prefix);
			}
		}
		return prefix;
		
	}

}
