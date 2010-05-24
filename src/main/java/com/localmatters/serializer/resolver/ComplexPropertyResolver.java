package com.localmatters.serializer.resolver;

import org.springframework.beans.factory.annotation.Required;

import com.localmatters.util.StringUtils;

/**
 * A delegating resolver that will tokenizes the property name and resolve each
 * level sequentially by calling the delegate resolver. Note that this resolver
 * will stop at any level that returns a null value and, in this case, return 
 * null.
 */
public class ComplexPropertyResolver extends DelegatingPropertyResolver {
	private String token;

	/**
	 * @see com.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
	public Object resolve(Object bean, String property) throws PropertyResolverException {
		if (StringUtils.isBlank(property)) {
			throw new InvalidPropertyException(property, bean.getClass().getName());
		}
		Object value = bean;
		String[] simpleProperties = StringUtils.splitByMultiByteDelimiter(property, getToken(), true);
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
	@Required
	public void setToken(String token) {
		this.token = token;
	}
}
