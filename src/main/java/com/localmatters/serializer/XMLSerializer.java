package com.localmatters.serializer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.config.Config;
import com.localmatters.util.CollectionUtils;

/**
 * This class defines a serializer that will output an object (tree) in XML.
 */
public class XMLSerializer implements Serializer {
	private static final String ROOT_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n%s";
	private static final String EMPTY_TAG_FORMAT = "<%s/>";
	private static final String SIMPLE_TAG_FORMAT = "<%s>%s</%s>";
	private static final String ATTRIBUTE_ONLY_TAG_FORMAT = "<%s%s/>";
	private static final String COMPLETE_TAG_FORMAT = "<%s%s>%s</%s>";
	private static final String EMPTY_ATTRIBUTE_FORMAT = " %s=\"\"";
	private static final String ATTRIBUTE_FORMAT = " %s=\"%s\"";

	/**
	 * @see com.localmatters.serializer.Serializer#serializeRoot(com.localmatters.serializer.config.Config, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeRoot(Config config, Object root, SerializationContext context) throws SerializationException {
		return String.format(ROOT_FORMAT, config.handle(root, context));
	}
	
	/**
	 * @see com.localmatters.serializer.Serializer#serializeValue(com.localmatters.serializer.config.Config, java.lang.Object, java.lang.String)
	 */
	public String serializeValue(Config config, Object value, SerializationContext context) {
		String name = config.getName();
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
		if (config.isWriteEmpty() && StringUtils.isNotBlank(name)) {
			return String.format(EMPTY_TAG_FORMAT, name);
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.Serializer#serializeComplex(com.localmatters.serializer.config.Config, java.util.Collection, java.util.Collection, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeComplex(Config config, Collection<Config> attributeConfigs, Collection<Config> elementConfigs, Object object, SerializationContext context) throws SerializationException {
		String name = config.getName();
		if (object != null) {
			StringBuilder attributes = new StringBuilder();
			if (CollectionUtils.isNotEmpty(attributeConfigs)) {
				for (Config attributeConfig : attributeConfigs) {
					attributes.append(attributeConfig.handle(object, context));
				}
			}
			
			StringBuilder elements = new StringBuilder();
			if (CollectionUtils.isNotEmpty(elementConfigs)) {
				for (Config elementConfig : elementConfigs) {
					elements.append(elementConfig.handle(object, context));
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
		if (config.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, name);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.localmatters.serializer.Serializer#serializeAttribute(com.localmatters.serializer.config.Config, java.lang.Object, java.lang.String)
	 */
	public String serializeAttribute(Config config, Object attribute, SerializationContext context) {
		if (attribute != null) {
			String serializedProperty = StringEscapeUtils.escapeXml(String.valueOf(attribute));
			return String.format(ATTRIBUTE_FORMAT, config.getName(), serializedProperty);
		}
		if (config.isWriteEmpty()) {
			return String.format(EMPTY_ATTRIBUTE_FORMAT, config.getName());
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * @see com.localmatters.serializer.Serializer#serializeIndex(com.localmatters.serializer.config.Config, com.localmatters.serializer.config.Config, java.util.Iterator, com.localmatters.serializer.SerializationContext)
	 */
	public String serializeIndex(Config config, Config elementConfig, Iterator<?> index, SerializationContext context) throws SerializationException {
		if (index.hasNext()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (index.hasNext()) {
				sb.append(elementConfig.handle(index.next(), context.appendIndex(i++)));
			}
			if (sb.length() > 0) {
				String name = config.getName();
				return String.format(SIMPLE_TAG_FORMAT, name, sb, name);
			}
		} 
		if (config.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, config.getName());
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
				sb.append(String.format(SIMPLE_TAG_FORMAT, key, value, key));
			}

			String name = config.getName();
			return String.format(SIMPLE_TAG_FORMAT, name, sb, name);
		}
		if (config.isWriteEmpty()) {
			return String.format(EMPTY_TAG_FORMAT, config.getName());
		}
		return StringUtils.EMPTY;
	}
	
	
}