package com.localmatters.serializer.serialization;


import org.apache.commons.lang.StringUtils;

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
 * <p>Also, sets the name under which to serialize the object if it has not been
 * done already.</p>
 */
public class PropertySerialization extends DelegatingSerialization {
	private String property;
	private String defaultName;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		Object value = null;
		if (obj != null) {
			try {
				value = ctx.getPropertyResolver().resolve(obj, getProperty());
			} catch (PropertyResolverException e) {
				throw new UnknownPropertyException(getProperty(), ctx, e);
			}
		}
		if ((value != null) || isWriteEmpty()) {
			getDelegate().serialize(ser, StringUtils.defaultString(name, getDefaultName()), value, ctx);
		}
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
		setDefaultName(property);
	}
    
    /**
     * @see com.localmatters.serializer.serialization.DelegatingSerialization#removeDefaultName()
     */
    @Override
    public String removeDefaultName() {
        super.removeDefaultName();
        String removed = getDefaultName();
        setDefaultName(null);
        return removed;
    }

    /**
     * @return The default name
     */
    protected String getDefaultName() {
        return defaultName;
    }

    /**
     * @param defaultName The default name
     */
    protected void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
	
    
}
