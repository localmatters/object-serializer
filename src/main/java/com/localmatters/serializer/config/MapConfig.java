package com.localmatters.serializer.config;

import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class describing the serialization configuration of a list (array, iterable, 
 * etc.)
 */
public class MapConfig extends AbstractConfig {
	private Config keyConfig;
	private Config valueConfig;

	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object obj, SerializationContext context) throws SerializationException {
		Map<?,?> map = null;
		
		if (obj instanceof Map<?,?>) {
			map = (Map<?,?>) obj;
		} else if (obj != null) {
			throw new MapExpectedException(context.appendSegment(getName()));
		}
		
		return context.getSerializer().serializeMap(this, getKeyConfig(), getValueConfig(), map, context.appendSegment(getName()));
	}

	/**
	 * @return The configuration of the map key
	 */
	public Config getKeyConfig() {
		return keyConfig;
	}

	/**
	 * @param keyConfig The configuration of the map key
	 */
	public void setKeyConfig(Config keyConfig) {
		this.keyConfig = keyConfig;
	}

	/**
	 * @return The configuration of the map value
	 */
	public Config getValueConfig() {
		return valueConfig;
	}

	/**
	 * @param valueConfig The configuration of the map value
	 */
	public void setValueConfig(Config valueConfig) {
		this.valueConfig = valueConfig;
	}
}
