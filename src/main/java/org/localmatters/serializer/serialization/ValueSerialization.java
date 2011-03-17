package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

/**
 * Class handling the serialization of a value
 */
public class ValueSerialization extends AbstractSerialization {

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		ctx.getWriter().writeValue(ser, name, obj, ctx);
	}

}
