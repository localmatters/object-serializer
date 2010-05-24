package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * This class enables defining a reference to another configuration, by simple
 * delegating to it 
 */
public class ReferenceConfig extends DelegatingConfig {

	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object object, SerializationContext context) throws SerializationException {
		return getDelegate().handle(object, context);
	}
}
