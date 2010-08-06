package com.localmatters.serializer.resolver;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Property resolver that uses the Apache BeanUtils's PropertyUtils to resolve
 * the property.
 */
public class BeanUtilsPropertyResolver implements PropertyResolver {
	private Pattern indexedMappedRemoverPattern;

	/**
	 * @see com.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	public Object resolve(Object bean, String property) throws InvalidPropertyException {
		if (bean == null) {
			return null;
		}
		try {
			// if the property is a list or a map, we first check that it is not
			// null as the PropertyUtils is, unfortunately not lenient 
			Matcher matcher = getIndexedMappedRemoverPattern().matcher(property);
			if (matcher.find()) {
				Object indexOrMap = PropertyUtils.getProperty(bean, matcher.group(1));
				if (indexOrMap == null) {
					return null;
				} 
				if (indexOrMap instanceof Iterable) {
					if (!((Iterable) indexOrMap).iterator().hasNext()) {
						return null;
					}
				} else if (indexOrMap instanceof Map) {
					if (((Map) indexOrMap).isEmpty()) {
						return null;
					}
				} else if (indexOrMap.getClass().isArray()) {
					if (Arrays.asList((Object[])indexOrMap).isEmpty()) {
						return null;
					}
				}
			}

			return PropertyUtils.getProperty(bean, property);
		} catch (Exception e) {
			throw new InvalidPropertyException(bean.getClass().getName(), property);
		}
	}

	/**
	 * @return The pattern to clean the property from any map or index 
	 * information. This pattern is expected to have a capturing group of the
	 * clean part of the property.
	 */
	public Pattern getIndexedMappedRemoverPattern() {
		return indexedMappedRemoverPattern;
	}

	/**
	 * @param indexedMappedRemoverPattern The pattern to clean the property from
	 * any map or index information. This pattern is expected to have a 
	 * capturing group of the clean part of the property.
	 */
	@Required
	public void setIndexedMappedRemoverPattern(Pattern indexedMappedRemoverPattern) {
		this.indexedMappedRemoverPattern = indexedMappedRemoverPattern;
	}

}
