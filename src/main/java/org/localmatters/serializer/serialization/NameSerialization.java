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
