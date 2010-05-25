package com.localmatters.serializer.serialization;

import java.util.Collection;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * This class handles the serialization of a complex structure; i.e. one that 
 * can have attributes and sub-elements.
 */
public class ComplexSerialization extends AbstractSerialization {
	private Collection<Serialization> attributeSerializations;
	private Collection<Serialization> elementSerializations;
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		return context.getSerializer().writeComplex(this, getAttributeSerializations(), getElementSerializations(), obj, context.appendSegment(getName()));
	}

	/**
	 * @return The attributes serializations
	 */
	public Collection<Serialization> getAttributeSerializations() {
		return attributeSerializations;
	}

	/**
	 * @param attributeSerializations The attributes serializations
	 */
	public void setAttributeSerializations(Collection<Serialization> attributeSerializations) {
		this.attributeSerializations = attributeSerializations;
	}

	/**
	 * @return The (sub-)elements serializations
	 */
	public Collection<Serialization> getElementSerializations() {
		return elementSerializations;
	}

	/**
	 * @param elementSerializations The (sub-)elements serializations
	 */
	public void setElementSerializations(Collection<Serialization> elementSerializations) {
		this.elementSerializations = elementSerializations;
	}
}
