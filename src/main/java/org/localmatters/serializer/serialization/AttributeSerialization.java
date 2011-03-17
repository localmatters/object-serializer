package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;


/**
 * Class handling the serialization of an attribute (as in XML attribute)
 */
public class AttributeSerialization extends ValueSerialization {

	/**
	 * @see org.localmatters.serializer.serialization.ValueSerialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	@Override
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		ctx.getWriter().writeAttribute(ser, name, obj, ctx);
	}

}
