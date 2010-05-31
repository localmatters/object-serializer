package com.localmatters.serializer.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.util.ReflectionUtils;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;

/**
 * This class defines a serialization writer that outputs JSON.
 */
public class JSONWriter extends AbstractWriter {
	private static final byte[] NULL = "null".getBytes();
	private static final byte[] LEFT_CURLY = "{".getBytes();
	private static final byte[] RIGHT_CURLY = "}".getBytes();
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
	 * @see com.localmatters.serializer.writer.Writer#writeRoot(com.localmatters.serializer.serialization.Serialization, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void writeRoot(Serialization ser, 
			Object root, 
			SerializationContext ctx) throws SerializationException {
		write(ctx, LEFT_CURLY);
		ser.serialize(ser, null, root, ctx);
		write(ctx, getPrefix(ctx)).write(ctx, RIGHT_CURLY);
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeValue(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void writeValue(Serialization ser, 
			String name,
			Object value, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(name);

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
				write(ctx, QUOTE).write(ctx, StringEscapeUtils.escapeXml(str)).write(ctx, QUOTE);
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
	 * @see com.localmatters.serializer.writer.Writer#writeAttribute(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void writeAttribute(Serialization ser, 
			String name,
			Object attribute, 
			SerializationContext ctx) throws SerializationException {
		writeValue(ser, name, attribute, ctx);
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeComplex(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, java.util.Collection, java.util.Collection, java.util.Collection, com.localmatters.serializer.SerializationContext)
	 */
	public void writeComplex(Serialization ser, 
			String name,
			Object object, 
			Collection<Serialization> attributes, 
			Collection<Serialization> elements, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(name);

		String prefix = getPrefix(ctx);
		if (object != null) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_CURLY);
			byte[] sep = "".getBytes();
			
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Serialization attribute : attributes) {
					write(ctx, sep);
					attribute.serialize(attribute, null, object, ctx);
					sep = COMMA;
				}
			}
			if (CollectionUtils.isNotEmpty(elements)) {
				for (Serialization element : elements) {
					write(ctx, sep);
					element.serialize(element, null, object, ctx);
					sep = COMMA;
				}
			}
			if (CollectionUtils.isNotEmpty(attributes) || CollectionUtils.isNotEmpty(elements)) {
				write(ctx, prefix);
			}
			write(ctx, RIGHT_CURLY);
		} else if (ser.isWriteEmpty()){
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_CURLY);
		}

		ctx.previousLevel();
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeIterator(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Iterator, java.lang.String, com.localmatters.serializer.serialization.Serialization, java.util.Collection, com.localmatters.serializer.SerializationContext)
	 */
	public void writeIterator(Serialization ser, 
			String name,
			Iterator<?> itr, 
			String elementName,
			Serialization element, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(name);

		String prefix = getPrefix(ctx);
		if (itr.hasNext()) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_SQUARE);
			byte[] sep = "".getBytes();
			while (itr.hasNext()) {
				write(ctx, sep);
				element.serialize(element, null, itr.next(), ctx);
				sep = COMMA;
			}
			write(ctx, prefix).write(ctx, RIGHT_SQUARE);
		} else if (ser.isWriteEmpty()) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_CLOSED_SQUARE);
		}

		ctx.previousLevel();
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeMap(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Map, java.lang.String, com.localmatters.serializer.serialization.Serialization, java.util.Collection, com.localmatters.serializer.SerializationContext)
	 */
	public void writeMap(Serialization ser, 
			String name,
			Map<?,?> map, 
			String key,
			Serialization value, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(name);

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(map)) {
			write(ctx, prefix).write(ctx, QUOTE).write(ctx, name).write(ctx, QUOTE_COLUMN_LEFT_CURLY);
			byte[] sep = "".getBytes();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				write(ctx, sep);
				value.serialize(value, resolvesMapKey(key, entry, ctx), entry.getValue(), ctx);
				sep = COMMA;
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
				for (int i=0; i<deepness; i++) {
					prefix += INDENTATION;
				}
				prefixes.put(deepness, prefix);
			}
		}
		return prefix;
		
	}

}
