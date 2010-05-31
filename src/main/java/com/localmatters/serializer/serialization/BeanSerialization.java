package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * A delegating serialization that can be used to get the object to serialize
 * from the map of beans provided in the context and pass it to its delegate. 
 * The name under which the object will be serialized as well as whether the 
 * object should be written if it is null or empty are inherit from the 
 * delegate.
 */
public class BeanSerialization extends DelegatingSerialization {
	private String bean;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object object, SerializationContext ctx) throws SerializationException {
		Object beanObject = ctx.getBean(getBean());
		if (beanObject == null) {
			throw new UnknownBeanException(getBean());	
		}
		getDelegate().serialize(ser, name, beanObject, ctx);
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
