package com.localmatters.serializer.serialization;

import java.util.ArrayList;
import java.util.List;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * This class handles the serialization of a complex structure; i.e. one that 
 * can have attributes and sub-elements.
 */
public class ComplexSerialization extends CommentSerialization {
	private List<Serialization> attributes;
	private List<Serialization> elements;
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		return context.getWriter().writeComplex(this, getComments(), getAttributes(), getElements(), obj, context.appendSegment(getName()));
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
