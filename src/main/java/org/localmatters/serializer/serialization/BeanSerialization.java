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
 * A delegating serialization that can be used to get the object to serialize
 * from the map of beans provided in the context and pass it to its delegate. 
 * The name under which the object will be serialized as well as whether the 
 * object should be written if it is null or empty are inherit from the 
 * delegate.
 */
public class BeanSerialization extends DelegatingSerialization {
	private String bean;

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object object, SerializationContext ctx) throws SerializationException {
		Object beanObject = ctx.getBean(getBean());
		if ((beanObject != null) || isWriteEmpty()) {
			getDelegate().serialize(ser, name, beanObject, ctx);
		}
	}

	/**
	 * @return The name of the bean to retrieve
	 */
	public String getBean() {
		return bean;
	}

	/**
	 * @param bean The name of the bean to retrieve
	 */
	public void setBean(String bean) {
		this.bean = bean;
	}
}
