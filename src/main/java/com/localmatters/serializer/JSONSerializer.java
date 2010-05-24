package com.localmatters.serializer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import com.localmatters.serializer.config.Config;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;

/**
 * This class defines a serializer that will output an object (tree) in JSON.
 */
public class JSONSerializer implements Serializer {
	@SuppressWarnings("unchecked")
	private static final Set NUMBER_CLASSES = CollectionUtils.asSet(Integer.class, Byte.class, Short.class, Double.class, Long.class, Float.class);
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
	 * @see com.localmatters.serializer.Serializer#serializeRoot(com.localmatters.serializer.config.Config, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeRoot(Config config, Object root, SerializationContext context) throws SerializationException {
		return String.format(ROOT_FORMAT, config.getName(), config.handle(root, context));
	}

	/**
	 * @see com.localmatters.serializer.Serializer#serializeValue(com.localmatters.serializer.config.Config, java.lang.Object, java.lang.String)
	 */
	public String serializeValue(Config config, Object value, SerializationContext context) {
		if (value == null) {
			if (config.isWriteEmpty()) {
				return NULL;
			}
		} else {
			String str = String.valueOf(value);
			if (NUMBER_CLASSES.contains(value.getClass())) {
				return str;
			}
			if (StringUtils.isNotEmpty(str)) { 
				return String.format(VALUE_FORMAT, StringEscapeUtils.escapeXml(str));
			}
			if (config.isWriteEmpty()) {
				return VALUE_EMPTY;
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.Serializer#serializeComplex(com.localmatters.serializer.config.Config, java.util.Collection, java.util.Collection, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeComplex(Config config, Collection<Config> attributeConfigs, Collection<Config> elementConfigs, Object object, SerializationContext context) throws SerializationException {
		if (object == null) {
			if (config.isWriteEmpty()) {
				return NULL;
			}
		} else {
			StringBuilder sb = new StringBuilder();
			if (CollectionUtils.isNotEmpty(attributeConfigs)) {
				for (Config attributeConfig : attributeConfigs) {
					String serializedProperty = attributeConfig.handle(object, context);
					if (StringUtils.isNotEmpty(serializedProperty)) { 
						StringUtils.addValue(sb, String.format(PROPERTY_FORMAT, attributeConfig.getName(), serializedProperty), SEPARATOR);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(elementConfigs)) {
				for (Config elementConfig : elementConfigs) {
					String serializedProperty = elementConfig.handle(object, context);
					if (StringUtils.isNotEmpty(serializedProperty)) { 
						StringUtils.addValue(sb, String.format(PROPERTY_FORMAT, elementConfig.getName(), serializedProperty), SEPARATOR);
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
	 * @see com.localmatters.serializer.Serializer#serializeAttribute(com.localmatters.serializer.config.Config, java.lang.Object, java.lang.String)
	 */
	public String serializeAttribute(Config config, Object attribute, SerializationContext context) {
		return serializeValue(config, attribute, context);
	}
	
	/**
	 * @see com.localmatters.serializer.Serializer#serializeIndex(com.localmatters.serializer.config.Config, com.localmatters.serializer.config.Config, java.util.Iterator, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeIndex(Config config, Config elementConfig, Iterator<?> index, SerializationContext context) throws SerializationException {
		if (index.hasNext()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (index.hasNext()) {
				String element = elementConfig.handle(index.next(), context.appendIndex(i++));
				StringUtils.addValue(sb, element, SEPARATOR);
			}
			return String.format(LIST_FORMAT, sb);
		} 
		if (config.isWriteEmpty()) {
			return LIST_EMPTY;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.Serializer#serializeMap(com.localmatters.serializer.config.Config, com.localmatters.serializer.config.Config, com.localmatters.serializer.config.Config, java.util.Map, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeMap(Config config, Config keyConfig, Config valueConfig, Map<?,?> map, SerializationContext context) throws SerializationException {
		if (CollectionUtils.isNotEmpty(map)) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String key = keyConfig.handle(entry.getKey(), context.appendMap());
				String value = valueConfig.handle(entry.getValue(), context.appendMap(key));
				StringUtils.addValue(sb, String.format(SIMPLE_MAP_ENTRY_FORMAT, key, value, key), SEPARATOR);
			}
			return String.format(BEAN_FORMAT, sb);
		}
		if (config.isWriteEmpty()) {
			return BEAN_EMPTY;
		}
		return StringUtils.EMPTY;
	}
}
