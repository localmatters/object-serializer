package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

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
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object object, SerializationContext ctx) throws SerializationException {
		getReferenced().serialize(ser, name, object, ctx);
	}
	
	/**
	 * @see org.localmatters.serializer.serialization.Serialization#isWriteEmpty()
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
