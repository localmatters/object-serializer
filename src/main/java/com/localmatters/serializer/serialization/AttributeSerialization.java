package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class handling the serialization of an attribute (as in XML attribute)
 */
public class AttributeSerialization extends ValueSerialization {

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	@Override
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		return context.getSerializer().writeAttribute(this, obj, context.appendSegment(getName()));
	}

}
