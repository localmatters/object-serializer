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
package org.localmatters.serializer.resolver;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Property resolver that uses the Apache BeanUtils's PropertyUtils to resolve
 * the property.
 */
public class BeanUtilsPropertyResolver implements PropertyResolver {
	private Pattern indexedMappedRemoverPattern;

	/**
	 * @see org.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	public Object resolve(Object bean, String property) throws InvalidPropertyException {
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
	public void setIndexedMappedRemoverPattern(Pattern indexedMappedRemoverPattern) {
		this.indexedMappedRemoverPattern = indexedMappedRemoverPattern;
	}

}
