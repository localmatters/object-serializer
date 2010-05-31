package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Class handling the serialization of a value
 */
public class ValueSerialization extends AbstractSerialization {

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		ctx.getWriter().writeValue(ser, name, obj, ctx);
	}

}
