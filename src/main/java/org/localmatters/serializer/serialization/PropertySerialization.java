/*
   Copyright 2010-present Local Matters, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.localmatters.serializer.serialization;


import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.resolver.PropertyResolverException;


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
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
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
     * @see org.localmatters.serializer.serialization.DelegatingSerialization#removeDefaultName()
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
