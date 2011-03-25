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
    private static final String INDENTATION = "   ";
    private static final String LEFT_CURLY = "{";
    private static final String LEFT_SQUARE = "[";
    private static final String QUOTE = "\"";
    private static final String QUOTE_COLUMN_LEFT_CURLY = "\": {";
    private static final String QUOTE_COLUMN_LEFT_SQUARE = "\": [";
    private static final String NEW_LINE = "\n";
    private static final String VALUE_LEVEL = "value";
    private static final String MAP_LEVEL = "map";
    private static final String ITERATOR_LEVEL = "iterator";
    private static final String COMPLEX_LEVEL = "complex";
    private static final byte[] NULL_BYTES = "null".getBytes();
	private static final byte[] LEFT_CURLY_BYTES = LEFT_CURLY.getBytes();
	private static final byte[] RIGHT_CURLY_BYTES = "}".getBytes();
	private static final byte[] RIGHT_SQUARE_BYTES = "]".getBytes();
	private static final byte[] QUOTE_BYTES = QUOTE.getBytes();
	private static final byte[] QUOTE_COLUMN_BYTES = "\": ".getBytes();
	private static final byte[] QUOTE_COLUMN_CLOSED_CURLY_BYTES = "\": {}".getBytes();
	private static final byte[] QUOTE_COLUMN_CLOSED_SQUARE_BYTES = "\": []".getBytes();
	private static final byte[] COMMA_BYTES = ", ".getBytes();
	private Map<Integer, String> prefixes = new HashMap<Integer, String>();

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeRoot(org.localmatters.serializer.serialization.Serialization, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeRoot(Serialization ser, 
			Object root, 
			SerializationContext ctx) throws SerializationException {
		Serialization contextless = ser.getContextlessSerialization();
		if (contextless instanceof ValueSerialization) {
			write(ctx, LEFT_CURLY_BYTES);
			ser.serialize(ser, null, root, ctx);
			write(ctx, getPrefix(ctx)).write(ctx, RIGHT_CURLY_BYTES);
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
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, VALUE_LEVEL));

		String prefix = getPrefix(ctx);
        String str = String.valueOf(value);
		if ((value != null) && StringUtils.isNotEmpty(str)) {
			write(ctx, prefix);
			if (StringUtils.isNotBlank(name)) {
				write(ctx, QUOTE_BYTES).write(ctx, name).write(ctx, QUOTE_COLUMN_BYTES);
			}

			if (ReflectionUtils.isNumeric(value.getClass()) || ReflectionUtils.isBoolean(value.getClass())) {
				write(ctx, str);
			} else {
				write(ctx, QUOTE_BYTES).write(ctx, EscapeUtils.escapeJson(str)).write(ctx, QUOTE_BYTES);
			}
		} else if (ser.isWriteEmpty()) {
			write(ctx, prefix);
			if (StringUtils.isNotBlank(name)) {
				write(ctx, QUOTE_BYTES).write(ctx, name).write(ctx, QUOTE_COLUMN_BYTES);
			}
			write(ctx, NULL_BYTES);
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
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, COMPLEX_LEVEL));
		boolean empty = true;

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(attributes) || CollectionUtils.isNotEmpty(elements)) {
			if (StringUtils.isEmpty(name)) {
			    ctx.pushPrefix(prefix + LEFT_CURLY);
			} else {
			    ctx.pushPrefix(prefix + QUOTE + name + QUOTE_COLUMN_LEFT_CURLY);
			}
			byte[] sep = new byte[]{};
			
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Serialization attribute : attributes) {
					ctx.pushPrefix(sep);
					attribute.serialize(attribute, null, object, ctx);
					if (ctx.consomePrefix() == null) {
						sep = COMMA_BYTES;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(elements)) {
				for (Serialization element : elements) {
					ctx.pushPrefix(sep);
					element.serialize(element, null, object, ctx);
					if (ctx.consomePrefix() == null) {
						sep = COMMA_BYTES;
					}
				}
			}
			if (ctx.consomePrefix() == null) {
			    write(ctx, prefix).write(ctx, RIGHT_CURLY_BYTES);
			    empty = false;
			}
		} 

		if (empty && ser.isWriteEmpty()){
			write(ctx, prefix).write(ctx, QUOTE_BYTES).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_CURLY_BYTES);
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
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, ITERATOR_LEVEL));
        boolean empty = true;

		String prefix = getPrefix(ctx);
		if (itr.hasNext()) {
			if (StringUtils.isEmpty(name)) {
			    ctx.pushPrefix(prefix + LEFT_SQUARE);
			} else {
			    ctx.pushPrefix(prefix + QUOTE + name + QUOTE_COLUMN_LEFT_SQUARE);
			}
			byte[] sep = new byte[]{};
			while (itr.hasNext()) {
				ctx.pushPrefix(sep);
				element.serialize(element, null, itr.next(), ctx);
				if (ctx.consomePrefix() == null) {
					sep = COMMA_BYTES;
				}
			}
            if (ctx.consomePrefix() == null) {
                write(ctx, prefix).write(ctx, RIGHT_SQUARE_BYTES);
                empty = false;
            }
		} 

        if (empty && ser.isWriteEmpty()){
			write(ctx, prefix).write(ctx, QUOTE_BYTES).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_SQUARE_BYTES);
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
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, MAP_LEVEL));
        boolean empty = true;

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(entries)) {
			if (StringUtils.isEmpty(name)) {
			    ctx.pushPrefix(prefix + LEFT_CURLY);
			} else {
			    ctx.pushPrefix(prefix + QUOTE + name + QUOTE_COLUMN_LEFT_CURLY);
			}
			byte[] sep = new byte[]{};
			for (Map.Entry entry : entries) {
				ctx.pushPrefix(sep);
				value.serialize(value, resolvesMapKey(key, entry, ctx), entry.getValue(), ctx);
				if (ctx.consomePrefix() == null) {
					sep = COMMA_BYTES;
				}
			}
            if (ctx.consomePrefix() == null) {
                write(ctx, prefix).write(ctx, RIGHT_CURLY_BYTES);
                empty = false;
            }
		} 

        if (empty && ser.isWriteEmpty()){
			write(ctx, prefix).write(ctx, QUOTE_BYTES).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_CURLY_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * Returns the prefix
	 * @param ctx The context
	 * @return The prefix
	 */
	protected String getPrefix(SerializationContext ctx) {
		String prefix = StringUtils.EMPTY;
		int deepness = ctx.getDeepness();
		if (ctx.isFormatting()) {
			prefix = prefixes.get(deepness);
			if (prefix == null) {
				prefix = NEW_LINE;
				for (int i=1; i<deepness; i++) {
					prefix += INDENTATION;
				}
				prefixes.put(deepness, prefix);
			}
		}
		return prefix;
		
	}

}
