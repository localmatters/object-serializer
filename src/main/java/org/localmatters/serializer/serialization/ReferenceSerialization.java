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
