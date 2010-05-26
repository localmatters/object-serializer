package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Class handling the serialization of a constant
 */
public class ConstantSerialization extends DelegatingSerialization {
	private Object constant;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		return getDelegate().serialize(getConstant(), context);
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
