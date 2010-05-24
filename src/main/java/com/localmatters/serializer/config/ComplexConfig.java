package com.localmatters.serializer.config;

import java.util.Collection;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class describing a the serialization configuration of a complex structure
 */
public class ComplexConfig extends AbstractConfig {
	private Collection<Config> attributeConfigs;
	private Collection<Config> elementConfigs;
	
	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object obj, SerializationContext context) throws SerializationException {
		return context.getSerializer().serializeComplex(this, getAttributeConfigs(), getElementConfigs(), obj, context.appendSegment(getName()));
	}

	/**
	 * @return The configuration of the attributes
	 */
	public Collection<Config> getAttributeConfigs() {
		return attributeConfigs;
	}

	/**
	 * @param attributeConfigs The configuration of the attributes
	 */
	public void setAttributeConfigs(Collection<Config> attributeConfigs) {
		this.attributeConfigs = attributeConfigs;
	}

	/**
	 * @return The configuration of the (sub-)elements
	 */
	public Collection<Config> getElementConfigs() {
		return elementConfigs;
	}

	/**
	 * @param elementConfigs The configuration of the (sub-)elements
	 */
	public void setElementConfigs(Collection<Config> elementConfigs) {
		this.elementConfigs = elementConfigs;
	}
}
