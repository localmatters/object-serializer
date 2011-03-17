package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

/**
 * A delegating serialization that can be used to sets the name under which the
 * object will be serialized
 */
public class NameSerialization extends DelegatingSerialization {
	private String name;

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
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
	 * @see org.localmatters.serializer.serialization.DelegatingSerialization#removeDefaultName()
	 */
	@Override
	public String removeDefaultName() {
	    super.removeDefaultName();
	    String removed = getName();
	    setName(null);
	    return removed;
	}
}
