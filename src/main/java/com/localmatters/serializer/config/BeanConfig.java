package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Class describing the serialization configuration of a bean. This class 
 * is responsible to retrieve the bean and to delegate the serialization to
 * its delegate. 
 */
public class BeanConfig extends DelegatingConfig {
	private String bean;

	/**
	 * Handles a property of the given bean
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object object, SerializationContext context) throws SerializationException {
		Object beanObject = context.getBean(getBean());
		if (beanObject == null) {
			throw new UnknownBeanException(getBean());	
		}

		return getDelegate().handle(beanObject, context);
	}

	/**
	 * @return The name of the bean to retrieve
	 */
	public String getBean() {
		return bean;
	}

	/**
	 * @param bean The name of the bean to retrieve
	 */
	public void setBean(String bean) {
		this.bean = bean;
	}
}
