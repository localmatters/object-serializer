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

import org.apache.commons.lang.StringUtils;


/**
 * A delegating resolver that will tokenizes the property name and resolve each
 * level sequentially by calling the delegate resolver. Note that this resolver
 * will stop at any level that returns a null value and, in this case, return 
 * null.
 */
public class ComplexPropertyResolver extends DelegatingPropertyResolver {
	private String token;

	/**
	 * @see org.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
	public Object resolve(Object bean, String property) throws PropertyResolverException {
		if (StringUtils.isBlank(property)) {
			throw new InvalidPropertyException(property, bean.getClass().getName());
		}
		Object value = bean;
		String[] simpleProperties = StringUtils.splitByWholeSeparatorPreserveAllTokens(property, getToken());
		for (String simpleProperty : simpleProperties) {
			if (StringUtils.isBlank(simpleProperty)) {
				throw new InvalidPropertyException(property, bean.getClass().getName());
			}
			value = getDelegate().resolve(value, simpleProperty);
			if (value == null) {
				return null;
			}
		}
		return value;
	}

	/**
	 * @return The token separating each level of the property
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token The token separating each level of the property
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
