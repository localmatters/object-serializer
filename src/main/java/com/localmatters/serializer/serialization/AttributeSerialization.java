package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class handling the serialization of an attribute (as in XML attribute)
 */
public class AttributeSerialization extends ValueSerialization {

	/**
	 * @see com.localmatters.serializer.serialization.ValueSerialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	@Override
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		ctx.getWriter().writeAttribute(ser, name, obj, ctx);
	}

}
