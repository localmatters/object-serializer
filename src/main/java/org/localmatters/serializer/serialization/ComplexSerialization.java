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

import java.util.ArrayList;
import java.util.List;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;



/**
 * This class handles the serialization of a complex structure; i.e. one that 
 * can have attributes and sub-elements.
 */
public class ComplexSerialization extends CommentSerialization {
	private List<Serialization> attributes;
	private List<Serialization> elements;

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#serialize(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		ctx.getWriter().writeComplex(ser, name, obj, getAttributes(), getElements(), getComments(), ctx);
	}

	/**
	 * @return The attributes serializations
	 */
	public List<Serialization> getAttributes() {
		if (attributes == null) {
			setAttributes(new ArrayList<Serialization>());
		}
		return attributes;
	}

	/**
	 * Adds the given attribute serialization
	 * @param attribute The attribute serialization to add
	 */
	public void addAttribute(Serialization attribute) {
		getAttributes().add(attribute);
	}
	
	/**
	 * @param attributes The attributes serializations
	 */
	public void setAttributes(List<Serialization> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return The (sub-)elements serializations
	 */
	public List<Serialization> getElements() {
		if (elements == null) {
			setElements(new ArrayList<Serialization>());
		}
		return elements;
	}

	/**
	 * Adds the given element serialization
	 * @param element The element serialization to add
	 */
	public void addElement(Serialization element) {
		getElements().add(element);
	}

	/**
	 * @param elements The (sub-)elements serializations
	 */
	public void setElements(List<Serialization> elements) {
		this.elements = elements;
	}
}
