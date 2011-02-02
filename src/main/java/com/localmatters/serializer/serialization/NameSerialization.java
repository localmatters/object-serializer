package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * A delegating serialization that can be used to sets the name under which the
 * object will be serialized
 */
public class NameSerialization extends DelegatingSerialization {
	private String name;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String aName, Object object, SerializationContext ctx) throws SerializationException {
		getDelegate().serialize(ser, getName(), object, ctx);
	}

	/**
	 * @return The name under which to serialize the object
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name under which to serialize the object
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see com.localmatters.serializer.serialization.DelegatingSerialization#removeDefaultName()
	 */
	@Override
	public String removeDefaultName() {
	    super.removeDefaultName();
	    String removed = getName();
	    setName(null);
	    return removed;
	}
}
