package com.localmatters.serializer.writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;

/**
 * This class defines a serialization writer that outputs XML.
 */
public class XMLWriter implements Writer {
	private static final String INDENTATION = "    ";
	private static final String ROOT_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>%s";
	private static final String EMPTY_TAG_FORMAT = "%s<%s/>";
	private static final String SIMPLE_TAG_FORMAT = "%s<%s>%s%s</%s>";
	private static final String ATTRIBUTE_ONLY_TAG_FORMAT = "%s<%s%s/>";
	private static final String COMPLETE_TAG_FORMAT = "%s<%s%s>%s%s</%s>";
	private static final String EMPTY_ATTRIBUTE_FORMAT = " %s=\"\"";
	private static final String ATTRIBUTE_FORMAT = " %s=\"%s\"";

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeRoot(com.localmatters.serializer.serialization.Serialization, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String writeRoot(Serialization serialization, Object root, SerializationContext context) throws SerializationException {
		return String.format(ROOT_FORMAT, serialization.serialize(root, context));
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeValue(com.localmatters.serializer.serialization.Serialization, java.lang.Object, java.lang.String)
	 */
	public String writeValue(Serialization serialization, Object value, SerializationContext context) {
		String name = serialization.getName();
		if (value != null) {
			String str = String.valueOf(value);
			if (StringUtils.isNotBlank(str)) {
				if (StringUtils.isNotBlank(name)) {
					return String.format(SIMPLE_TAG_FORMAT, getPrefix(context), name, StringEscapeUtils.escapeXml(str), getPrefix(context), name);
				} else {
					return StringEscapeUtils.escapeXml(str);
				}
			}
		}
		if (serialization.isWriteEmpty() && StringUtils.isNotBlank(name)) {
			return String.format(EMPTY_TAG_FORMAT, getPrefix(context), name);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets the prefix taking the comments into account
	 * @param context The context
	 * @param comments The comments to write 
	 * @return The string describing the comment
	 */
	protected String writePrefixWithComments(SerializationContext context, Collection<String> comments) {
		if (context.isPretty()) {
			String prefix = getPrefix(context);
			StringBuilder sb = new StringBuilder();
			if (CollectionUtils.isNotEmpty(comments)) {
				sb.append("\n").append(prefix).append("<!-- ");
				String sep = ""; 
				for (String comment : comments) {
					StringUtils.addValue(sb, comment.replaceAll("--", "**"), sep);
					sep = prefix + "     ";
				}
				sb.append(" -->");
			}
			sb.append(prefix);
			return sb.toString();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeComplex(com.localmatters.serializer.serialization.Serialization, java.util.Collection, java.util.Collection, java.util.Collection, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String writeComplex(Serialization serialization, Collection<String> comments, Collection<Serialization> attributeSerializations, Collection<Serialization> elementSerializations, Object object, SerializationContext context) throws SerializationException {
		String name = serialization.getName();
		if (object != null) {
			StringBuilder attributes = new StringBuilder();
			if (CollectionUtils.isNotEmpty(attributeSerializations)) {
				for (Serialization attributeSerialization : attributeSerializations) {
					attributes.append(attributeSerialization.serialize(object, context));
				}
			}
			
			StringBuilder elements = new StringBuilder();
			if (CollectionUtils.isNotEmpty(elementSerializations)) {
				for (Serialization elementSerialization : elementSerializations) {
					elements.append(elementSerialization.serialize(object, context));
				}
			}
			
			if ((attributes.length() == 0) && (elements.length() == 0)) {
				return String.format(EMPTY_TAG_FORMAT, writePrefixWithComments(context, comments), name);
			}
			if (attributes.length() == 0) {
				return String.format(SIMPLE_TAG_FORMAT, writePrefixWithComments(context, comments), name, elements, getPrefix(context), name);
			}
			if (elements.length() == 0) {
				return String.format(ATTRIBUTE_ONLY_TAG_FORMAT, writePrefixWithComments(context, comments), name, attributes, name);
			}
			return String.format(COMPLETE_TAG_FORMAT, writePrefixWithComments(context, comments), name, attributes, elements, getPrefix(context), name);
			
		}
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, writePrefixWithComments(context, comments), name);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeAttribute(com.localmatters.serializer.serialization.Serialization, java.lang.Object, java.lang.String)
	 */
	public String writeAttribute(Serialization serialization, Object attribute, SerializationContext context) {
		if (attribute != null) {
			String serializedProperty = StringEscapeUtils.escapeXml(String.valueOf(attribute));
			return String.format(ATTRIBUTE_FORMAT, serialization.getName(), serializedProperty);
		}
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_ATTRIBUTE_FORMAT, serialization.getName());
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.writer.Writer#writeIterator(com.localmatters.serializer.serialization.Serialization, java.util.Collection, com.localmatters.serializer.serialization.Serialization, java.util.Iterator, com.localmatters.serializer.SerializationContext)
	 */
	public String writeIterator(Serialization serialization, Collection<String> comments, Serialization elementSerialization, Iterator<?> index, SerializationContext context) throws SerializationException {
		if (index.hasNext()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (index.hasNext()) {
				sb.append(elementSerialization.serialize(index.next(), context.appendIndex(i++)));
			}
			if (sb.length() > 0) {
				String name = serialization.getName();
				return String.format(SIMPLE_TAG_FORMAT, writePrefixWithComments(context, comments), name, sb, getPrefix(context), name);
			}
		} 
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, writePrefixWithComments(context, comments), serialization.getName());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeMap(com.localmatters.serializer.serialization.Serialization, java.util.Collection, com.localmatters.serializer.serialization.Serialization, com.localmatters.serializer.serialization.Serialization, java.util.Map, com.localmatters.serializer.SerializationContext)
	 */
	public String writeMap(Serialization serialization, Collection<String> comments, Serialization keySerialization, Serialization valueSerialization, Map<?,?> map, SerializationContext context) throws SerializationException {
		if (CollectionUtils.isNotEmpty(map)) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String key = keySerialization.serialize(entry.getKey(), context.appendMap());
				String value = valueSerialization.serialize(entry.getValue(), context.appendMap(key));
				sb.append(String.format(SIMPLE_TAG_FORMAT, writePrefixWithComments(context, comments), key, value, getPrefix(context), key));
			}

			String name = serialization.getName();
			return String.format(SIMPLE_TAG_FORMAT, writePrefixWithComments(context, comments), name, sb, getPrefix(context), name);
		}
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, writePrefixWithComments(context, comments), serialization.getName());
		}
		return StringUtils.EMPTY;
	}

	protected String getPrefix(SerializationContext context) {
		String prefix = "";
		int deepness = context.getDeepness();
		if (context.isPretty() && (deepness > 0)) {
			prefix = "\n";
			for (int i=1; i<deepness; i++) {
				prefix += INDENTATION;
			}
		}
		return prefix;
		
	}
	
	
}