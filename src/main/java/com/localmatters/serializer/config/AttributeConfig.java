package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class describing the serialization configuration of an attribute
 */
public class AttributeConfig extends ValueConfig {

	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	@Override
	public String handle(Object obj, SerializationContext context) throws SerializationException {
		return context.getSerializer().serializeAttribute(this, obj, context.appendSegment(getName()));
	}

}
