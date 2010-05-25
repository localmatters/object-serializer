package com.localmatters.serializer.writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.util.CollectionUtils;

/**
 * This class defines a serialization writer that outputs XML.
 */
public class XMLWriter implements Writer {
	private static final String ROOT_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n%s";
	private static final String EMPTY_TAG_FORMAT = "<%s/>";
	private static final String SIMPLE_TAG_FORMAT = "<%s>%s</%s>";
	private static final String ATTRIBUTE_ONLY_TAG_FORMAT = "<%s%s/>";
	private static final String COMPLETE_TAG_FORMAT = "<%s%s>%s</%s>";
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
					return String.format(SIMPLE_TAG_FORMAT, name, StringEscapeUtils.escapeXml(str), name);
				} else {
					return StringEscapeUtils.escapeXml(str);
				}
			}
		}
		if (serialization.isWriteEmpty() && StringUtils.isNotBlank(name)) {
			return String.format(EMPTY_TAG_FORMAT, name);
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.writer.Writer#writeComplex(com.localmatters.serializer.serialization.Serialization, java.util.Collection, java.util.Collection, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String writeComplex(Serialization serialization, Collection<Serialization> attributeSerializations, Collection<Serialization> elementSerializations, Object object, SerializationContext context) throws SerializationException {
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
				return String.format(EMPTY_TAG_FORMAT, name);
			}
			if (attributes.length() == 0) {
				return String.format(SIMPLE_TAG_FORMAT, name, elements, name);
			}
			if (elements.length() == 0) {
				return String.format(ATTRIBUTE_ONLY_TAG_FORMAT, name, attributes, name);
			}
			return String.format(COMPLETE_TAG_FORMAT, name, attributes, elements, name);
			
		}
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, name);
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
	 * @see com.localmatters.serializer.writer.Writer#writeIterator(com.localmatters.serializer.serialization.Serialization, com.localmatters.serializer.serialization.Serialization, java.util.Iterator, com.localmatters.serializer.SerializationContext)
	 */
	public String writeIterator(Serialization serialization, Serialization elementSerialization, Iterator<?> index, SerializationContext context) throws SerializationException {
		if (index.hasNext()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (index.hasNext()) {
				sb.append(elementSerialization.serialize(index.next(), context.appendIndex(i++)));
			}
			if (sb.length() > 0) {
				String name = serialization.getName();
				return String.format(SIMPLE_TAG_FORMAT, name, sb, name);
			}
		} 
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, serialization.getName());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeMap(com.localmatters.serializer.serialization.Serialization, com.localmatters.serializer.serialization.Serialization, com.localmatters.serializer.serialization.Serialization, java.util.Map, com.localmatters.serializer.SerializationContext)
	 */
	public String writeMap(Serialization serialization, Serialization keySerialization, Serialization valueSerialization, Map<?,?> map, SerializationContext context) throws SerializationException {
		if (CollectionUtils.isNotEmpty(map)) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String key = keySerialization.serialize(entry.getKey(), context.appendMap());
				String value = valueSerialization.serialize(entry.getValue(), context.appendMap(key));
				sb.append(String.format(SIMPLE_TAG_FORMAT, key, value, key));
			}

			String name = serialization.getName();
			return String.format(SIMPLE_TAG_FORMAT, name, sb, name);
		}
		if (serialization.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, serialization.getName());
		}
		return StringUtils.EMPTY;
	}
	
	
}