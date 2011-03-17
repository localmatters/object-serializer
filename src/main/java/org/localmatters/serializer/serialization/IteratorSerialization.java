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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;


/**
 * This class handles the serialization of an object that can be iterated over;
 * i.e. an Iterable, an Array or, even, a Map (through its Set of Map.Entry).
 */
public class IteratorSerialization extends CommentSerialization {
	protected static final Iterator<?> EMTPY_ITERATOR = Collections.EMPTY_LIST.iterator();
	private String elementName;
	private Serialization element;

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		Iterator<?> itr = null;
		
		// get an iterator over the array, iterable or map's entries
		if (obj == null) {
			itr = EMTPY_ITERATOR;
		} else if (obj.getClass().isArray()) {
			itr = Arrays.asList((Object[]) obj).iterator();
		} else if (obj instanceof Iterable<?>) {
			itr = ((Iterable<?>) obj).iterator();
		} else if (obj instanceof Map<?, ?>) {
			itr = ((Map<?, ?>) obj).entrySet().iterator();
		} else {
			// if the object is not an array or an iterable, there is a
			// configuration problem
			throw new IteratorExpectedException(ctx.nextLevel(name));
		}
		
		ctx.getWriter().writeIterator(ser, name, itr, getElementName(), getElement(), getComments(), ctx);
	}

	/**
	 * @return The name under which each element should be serialized
	 */
	protected String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName The name under which each element should be serialized
	 */
	protected void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return The index elements serialization
	 */
	public Serialization getElement() {
		return element;
	}

	/**
	 * @param element The index elements serialization
	 */
	public void setElement(Serialization element) {
	    setElementName(element.removeDefaultName());
	    this.element = element;
	}
}
