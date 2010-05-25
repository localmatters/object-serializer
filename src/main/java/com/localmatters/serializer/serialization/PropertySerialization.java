package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.resolver.PropertyResolverException;


/**
 * <p>A delegating serialization that can be used to get the object to serialize
 * from a property of the given object and pass it to its delegate. To resolve
 * the property,it uses the property resolver defined in the context.</p> 
 * <p>The name under which the object will be serialized as well as whether the
 * object should be written if it is null or empty are inherit from the 
 * delegate.</p>
 */
public class PropertySerialization extends DelegatingSerialization {
	private String property;
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		Object value = null;
		try {
			value = context.getPropertyResolver().resolve(obj, getProperty());
		} catch (PropertyResolverException e) {
			throw new UnknownPropertyException(getProperty(), context, e);
		}
		return getDelegate().serialize(value, context);
	}

	/**
	 * @return The property to resolve
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property The property to resolve
	 */
	public void setProperty(String property) {
		this.property = property;
	}
}
