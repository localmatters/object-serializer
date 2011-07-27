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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines a property resolver that can dispatch the resolution 
 * to other resolver based on the name-space prefixing the property expression
 * (e.g. <code>mvel:</code>)
 */
public class DispatchingPropertyResolver implements PropertyResolver {
    private static final Pattern NS_PATTERN = Pattern.compile("^([^:]+):(.+)$"); 
	private PropertyResolver defaultResolver;
	private Map<String, PropertyResolver> resolvers;

	/**
	 * @see org.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
    public Object resolve(Object bean, String property) throws PropertyResolverException {
        Matcher matcher = NS_PATTERN.matcher(property);
        if (matcher.matches()) {
            String ns = matcher.group(1);
            PropertyResolver resolver = getResolvers().get(ns);
            if (resolver != null) {
                return resolver.resolve(bean, matcher.group(2));
            }
        }
        return getDefault().resolve(bean, property);
    }
	
	
	/**
	 * @return The default resolver
	 */
	public PropertyResolver getDefault() {
		return defaultResolver;
	}

	/**
	 * @param defaultResolver The default resolver
	 */
	public void setDefault(PropertyResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

    /**
     * @return The map of resolvers per name-space
     */
    public Map<String, PropertyResolver> getResolvers() {
        return resolvers;
    }

    /**
     * @param resolvers The map of resolvers per name-space
     */
    public void setResolvers(Map<String, PropertyResolver> resolvers) {
        this.resolvers = resolvers;
    }
}
