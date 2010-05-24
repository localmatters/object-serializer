package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;



/**
 * Class describing the serialization configuration of a value
 */
public class ValueConfig extends AbstractConfig {

	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object obj, SerializationContext context) throws SerializationException {
		return context.getSerializer().serializeValue(this, obj, context.appendSegment(getName()));
	}

}
