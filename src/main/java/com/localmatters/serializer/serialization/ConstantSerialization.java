package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Class handling the serialization of a constant
 */
public class ConstantSerialization extends DelegatingSerialization {
	private Object constant;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		getDelegate().serialize(ser, name, getConstant(), ctx);
	}

	/**
	 * @return The constant
	 */
	public Object getConstant() {
		return constant;
	}

	/**
	 * @param constant The constant
	 */
	public void setConstant(Object constant) {
		this.constant = constant;
	}
}
