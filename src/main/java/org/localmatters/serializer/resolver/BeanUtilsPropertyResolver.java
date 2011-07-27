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
import org.apache.commons.lang.StringUtils;

/**
 * Property resolver that uses the Apache BeanUtils's PropertyUtils to resolve
 * the property.
 */
public class BeanUtilsPropertyResolver implements PropertyResolver {
	private static final Pattern INDEX_MAP_PATTERN = Pattern.compile("^(([^\\[\\(]+)(?:\\[|\\()[^\\]\\)]+(?:\\]|\\)))(.*)$");
	private static final String VALUE = "value";

    /**
     * @see org.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
     */
    public Object resolve(Object bean, String property) throws InvalidPropertyException {
        return resolve(bean, property, bean, property);
    }
    
    @SuppressWarnings("rawtypes")
   	private Object resolve(Object bean, String property, Object originalBean, String originalProperty) throws InvalidPropertyException {
		try {
			// if the property is a list or a map, we first check that it is not
			// null as the PropertyUtils is, unfortunately not lenient 
			Matcher matcher = INDEX_MAP_PATTERN.matcher(property);
			if (matcher.find()) {
				Object indexOrMap = PropertyUtils.getProperty(bean, matcher.group(2));
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

				String composite = matcher.group(3);
                if (StringUtils.isNotBlank(composite)) {
				    return resolve(new ListMapWrapper(PropertyUtils.getProperty(bean, matcher.group(1))), VALUE + composite, originalBean, originalProperty);
				}
			}

			return PropertyUtils.getProperty(bean, property);
		} catch (Exception e) {
			throw new InvalidPropertyException(originalProperty, originalBean.getClass().getName());
		}
	}

	/**
	 * Wrapper of list and maps to slip the resolution of any composition (the
	 * property util expect a class with a property that is a map or a list, not
	 * one of them directly)
	 */
	public class ListMapWrapper {
	    private Object value;

	    /**
	     * Default constructor with the specification of the value
	     * @param value
	     */
	    public ListMapWrapper(Object value) {
	        setValue(value);
	    }
	    
        /**
         * @return The value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value The value
         */
        public void setValue(Object value) {
            this.value = value;
        }
	}
	
}
