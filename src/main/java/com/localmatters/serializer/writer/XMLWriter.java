package com.localmatters.serializer.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.util.EscapeUtils;

/**
 * This class defines a serialization writer that outputs XML.
 */
public class XMLWriter extends AbstractWriter {
	private static final byte[] ROOT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes();
	private static final byte[] LT = "<".getBytes();
	private static final byte[] LT_SLASH = "</".getBytes();
	private static final byte[] GT = ">".getBytes();
	private static final byte[] SLASH_GT = "/>".getBytes();
	private static final byte[] SPACE = " ".getBytes();
	private static final byte[] EQUALS_QUOTE = "=\"".getBytes();
	private static final byte[] QUOTE = "\"".getBytes();
	private static final String INDENTATION = "    ";
	private Map<Integer, String> prefixes = new HashMap<Integer, String>();

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeRoot(com.localmatters.serializer.serialization.Serialization, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void writeRoot(Serialization ser, Object root, SerializationContext ctx) throws SerializationException {
		write(ctx, ROOT);
		ser.serialize(ser, null, root, ctx);
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeValue(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void writeValue(Serialization ser, 
			String name,
			Object value, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, "value"));

		String prefix = getPrefix(ctx);
		if (value != null) {
			String str = EscapeUtils.escapeXml(String.valueOf(value));
			if (StringUtils.isNotBlank(name)) {
				write(ctx, prefix)
				.write(ctx, LT).write(ctx, name).write(ctx, GT)
				.write(ctx, str)
				.write(ctx, LT_SLASH).write(ctx, name).write(ctx, GT);
			} else {
				write(ctx, str);
			}
		} else if (ser.isWriteEmpty() && StringUtils.isNotBlank(name)) {
			write(ctx, prefix).write(ctx, LT).write(ctx, name).write(ctx, SLASH_GT);
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
		ctx.nextLevel(checkRequiredName(ctx, name));

		if (attribute != null) {
			write(ctx, SPACE)
			.write(ctx, name)
			.write(ctx, EQUALS_QUOTE)
			.write(ctx, EscapeUtils.escapeXml(String.valueOf(attribute)))
			.write(ctx, QUOTE);
		} else if (ser.isWriteEmpty()) {
			write(ctx, SPACE)
			.write(ctx, name)
			.write(ctx, EQUALS_QUOTE)
			.write(ctx, QUOTE);
		}

		ctx.previousLevel();
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
		ctx.nextLevel(checkRequiredName(ctx, name));

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(attributes) || CollectionUtils.isNotEmpty(elements)) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name);

			// writes the attributes
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Serialization attribute : attributes) {
					attribute.serialize(attribute, null, object, ctx);
				}
			}

			// closes the tag if there are no element or writes them
			if (CollectionUtils.isEmpty(elements)) {
				write(ctx, SLASH_GT);
			} else {
				write(ctx, GT);
				for (Serialization element : elements) {
					element.serialize(element, null, object, ctx);
				}
				write(ctx, prefix).write(ctx, LT_SLASH).write(ctx, name).write(ctx, GT);
			}
			
		} else if (ser.isWriteEmpty()) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name).write(ctx, SLASH_GT);
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
		ctx.nextLevel(checkRequiredName(ctx, name));

		String prefix = getPrefix(ctx);
		if (itr.hasNext()) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name).write(ctx, GT);
			while (itr.hasNext()) {
				element.serialize(element, elementName, itr.next(), ctx);
			}
			write(ctx, prefix).write(ctx, LT_SLASH).write(ctx, name).write(ctx, GT);
		} else if (ser.isWriteEmpty()) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name).write(ctx, SLASH_GT);
		}

		ctx.previousLevel();
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeMap(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Collection, java.lang.String, com.localmatters.serializer.serialization.Serialization, java.util.Collection, com.localmatters.serializer.SerializationContext)
	 */
	@SuppressWarnings("rawtypes")
	public void writeMap(Serialization ser, 
			String name,
			Collection<Map.Entry> entries, 
			String key,
			Serialization value, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(checkRequiredName(ctx, name));

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(entries)) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name).write(ctx, GT);
			for (Map.Entry entry : entries) {
				value.serialize(value, resolvesMapKey(key, entry, ctx), entry.getValue(), ctx);
			}
			write(ctx, prefix).write(ctx, LT_SLASH).write(ctx, name).write(ctx, GT);

		} else if (ser.isWriteEmpty()) {
			writeComments(ctx, prefix, comments).write(ctx, LT).write(ctx, name).write(ctx, SLASH_GT);
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

	/**
	 * Writes the comments, if any, otherwise, just writes the prefix
	 * @param ctx The context
	 * @param prefix The prefix 
	 * @param comments The comments to write 
	 * @return The writer itself for ease of coding
	 * @throws SerializationException When the writing failed
	 */
	protected XMLWriter writeComments(SerializationContext ctx, 
			String prefix, 
			Collection<String> comments) throws SerializationException {
		if (ctx.isFormatting()) {
			if (CollectionUtils.isNotEmpty(comments)) {
				String sep = "\n" + prefix + "<!-- "; 
				for (String comment : comments) {
					write(ctx, sep);
					write(ctx, comment.replaceAll("--", "**"));
					sep = prefix + "     ";
				}
				write(ctx, " -->");
			}
			write(ctx, prefix);
		}
		return this;
	}
}