package com.localmatters.serializer.writer;

import java.util.Collection;
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
public class JSONWriter implements Writer {
	private static final String ROOT_FORMAT = "{\"%s\":%s}";
	private static final String SEPARATOR = ",";
	private static final String NULL = "null";
	private static final String VALUE_FORMAT = "\"%s\"";
	private static final String VALUE_EMPTY = "\"\"";
	private static final String LIST_EMPTY = "[]";
	private static final String LIST_FORMAT = "[%s]";
	private static final String BEAN_EMPTY = "{}";
	private static final String BEAN_FORMAT = "{%s}";
	private static final String PROPERTY_FORMAT = "\"%s\":%s";
	private static final String SIMPLE_MAP_ENTRY_FORMAT = "%s:%s";

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeRoot(com.localmatters.serializer.serialization.Serialization, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String writeRoot(Serialization serialization, Object root, SerializationContext context) throws SerializationException {
		return String.format(ROOT_FORMAT, serialization.getName(), serialization.serialize(root, context));
	}

	/**
	 * @see com.localmatters.serializer.writer.Writer#writeValue(com.localmatters.serializer.serialization.Serialization, java.lang.Object, java.lang.String)
	 */
	public String writeValue(Serialization serialization, Object value, SerializationContext context) {
		if (value == null) {
			if (serialization.isWriteEmpty()) {
				return NULL;
			}
		} else {
			String str = String.valueOf(value);
			if (ReflectionUtils.isNumeric(value.getClass()) || ReflectionUtils.isBoolean(value.getClass())) {
				return str;
			}
			if (StringUtils.isNotEmpty(str)) { 
				return String.format(VALUE_FORMAT, StringEscapeUtils.escapeXml(str));
			}
			if (serialization.isWriteEmpty()) {
				return VALUE_EMPTY;
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.writer.Writer#writeComplex(com.localmatters.serializer.serialization.Serialization, java.util.Collection, java.util.Collection, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String writeComplex(Serialization serialization, Collection<Serialization> attributeSerializations, Collection<Serialization> elementSerializations, Object object, SerializationContext context) throws SerializationException {
		if (object == null) {
			if (serialization.isWriteEmpty()) {
				return NULL;
			}
		} else {
			StringBuilder sb = new StringBuilder();
			if (CollectionUtils.isNotEmpty(attributeSerializations)) {
				for (Serialization attributeSerialization : attributeSerializations) {
					String serializedProperty = attributeSerialization.serialize(object, context);
					if (StringUtils.isNotEmpty(serializedProperty)) { 
						StringUtils.addValue(sb, String.format(PROPERTY_FORMAT, attributeSerialization.getName(), serializedProperty), SEPARATOR);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(elementSerializations)) {
				for (Serialization elementSerialization : elementSerializations) {
					String serializedProperty = elementSerialization.serialize(object, context);
					if (StringUtils.isNotEmpty(serializedProperty)) { 
						StringUtils.addValue(sb, String.format(PROPERTY_FORMAT, elementSerialization.getName(), serializedProperty), SEPARATOR);
					}
				}
			}
			if (sb.length() == 0) {
				return BEAN_EMPTY;
			}
			
			return String.format(BEAN_FORMAT, sb);
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.writer.Writer#writeAttribute(com.localmatters.serializer.serialization.Serialization, java.lang.Object, java.lang.String)
	 */
	public String writeAttribute(Serialization serialization, Object attribute, SerializationContext context) {
		return writeValue(serialization, attribute, context);
	}
	
	/**
	 * @see com.localmatters.serializer.writer.Writer#writeIterator(com.localmatters.serializer.serialization.Serialization, com.localmatters.serializer.serialization.Serialization, java.util.Iterator, com.localmatters.serializer.SerializationContext)
	 */
	public String writeIterator(Serialization serialization, Serialization elementSerialization, Iterator<?> index, SerializationContext context) throws SerializationException {
		if (index.hasNext()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (index.hasNext()) {
				String element = elementSerialization.serialize(index.next(), context.appendIndex(i++));
				StringUtils.addValue(sb, element, SEPARATOR);
			}
			return String.format(LIST_FORMAT, sb);
		} 
		if (serialization.isWriteEmpty()) {
			return LIST_EMPTY;
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
				StringUtils.addValue(sb, String.format(SIMPLE_MAP_ENTRY_FORMAT, key, value, key), SEPARATOR);
			}
			return String.format(BEAN_FORMAT, sb);
		}
		if (serialization.isWriteEmpty()) {
			return BEAN_EMPTY;
		}
		return StringUtils.EMPTY;
	}
}
