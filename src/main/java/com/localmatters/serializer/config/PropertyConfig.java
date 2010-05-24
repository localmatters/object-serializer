package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.resolver.PropertyResolverException;


/**
 * Class describing the serialization configuration of a property. This class 
 * is responsible to retrieve the property and to delegate the serialization to
 * its delegate. 
 */
public class PropertyConfig extends DelegatingConfig {
	private String property;
	
	/**
	 * Handles a property of the given bean
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object bean, SerializationContext context) throws SerializationException {
		Object value = null;
		try {
			value = context.getPropertyResolver().resolve(bean, getProperty());
		} catch (PropertyResolverException e) {
			throw new UnknownPropertyException(getProperty(), context, e);
		}
		return getDelegate().handle(value, context);
	}

	/**
	 * @return The property to resolve
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property The property to resolve
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
