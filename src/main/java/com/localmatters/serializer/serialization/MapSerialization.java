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
public class MapSerialization extends CommentSerialization {
	private String key;
	private Serialization value;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		Map<?,?> map = null;
		if (obj instanceof Map<?,?>) {
			map = (Map<?,?>) obj;
		} else if (obj != null) {
			throw new MapExpectedException(ctx.nextLevel(name));
		}

	ctx.getWriter().writeMap(ser, name, map, getKey(), getValue(), getComments(), ctx);
	}

	/**
	 * @return The property to resolve the key or null if the key should be
	 * serialized "as it"
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key The property to resolve the key or null if the key should be
	 * serialized "as it"
	 */
	public void setKey(String key) {
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
