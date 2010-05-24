package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class describing the configuration of an object(s) serialization
 */
public interface Config {
	
	/**
	 * Handles the given object serialization
	 * @param obj The object to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When an error is found in the config
	 */
	public String handle(Object obj, SerializationContext context) throws SerializationException;

	/**
	 * @return The name under which the object will be serialized
	 */
	public String getName();
	
	/**
	 * @return Whether a null or empty object should be written
	 */
	public boolean isWriteEmpty();
	
}
