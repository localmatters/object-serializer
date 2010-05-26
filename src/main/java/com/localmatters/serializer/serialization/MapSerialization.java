package com.localmatters.serializer.serialization;

import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * This class handles the serialization of a simple map; i.e. a map for which 
 * the keys will be serialized simply as a value. The 
 * <code>IteratorSerialization</code> can be used to handle maps for which the 
 * keys should be serialized as more complex elements. 
 */
public class MapSerialization extends AbstractSerialization {
	private Serialization key;
	private Serialization value;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		Map<?,?> map = null;
		if (obj instanceof Map<?,?>) {
			map = (Map<?,?>) obj;
		} else if (obj != null) {
			throw new MapExpectedException(context.appendSegment(getName()));
		}
		
		return context.getWriter().writeMap(this, getKey(), getValue(), map, context.appendSegment(getName()));
	}

	/**
	 * @return The keys serialization
	 */
	public Serialization getKey() {
		return key;
	}

	/**
	 * @param key The keys serialization
	 */
	public void setKey(Serialization key) {
		this.key = key;
	}

	/**
	 * @return The values serialization
	 */
	public Serialization getValue() {
		return value;
	}

	/**
	 * @param value The values serialization
	 */
	public void setValue(Serialization value) {
		this.value = value;
	}
}
