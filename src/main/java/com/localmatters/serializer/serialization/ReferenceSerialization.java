package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.util.StringUtils;

/**
 * A special serialization that can be used to implement a reference to another
 * serialization. The name under which the object will be serialized as well as 
 * whether the object should be written if it is null or empty can be either
 * defines specifically for this reference or inherit from the referenced 
 * handler if they are not defined in this handler.
 */
public class ReferenceSerialization extends AbstractSerialization {
	private Serialization referenced;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object object, SerializationContext context) throws SerializationException {
		return getReferenced().serialize(object, context);
	}
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#getName()
	 */
	@Override
	public String getName() {
		String name = super.getName();
		if (StringUtils.isBlank(name)) {
			if (getReferenced() != null) {
				return getReferenced().getName();
			}
		}
		return name;
	}
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#isWriteEmpty()
	 */
	@Override
	public boolean isWriteEmpty() {
		Boolean writeEmpty = getWriteEmpty();
		if (writeEmpty == null) {
			if (getReferenced() != null) {
				return getReferenced().isWriteEmpty();
			}
			return true;
		}
		return writeEmpty;
	}

	/**
	 * @return The referenced handler
	 */
	public Serialization getReferenced() {
		return referenced;
	}

	/**
	 * @param referenced The referenced handler
	 */
	public void setReferenced(Serialization referenced) {
		this.referenced = referenced;
	}
}
