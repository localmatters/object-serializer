package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * <p>Classes that will handle the serialization of a given object, property, 
 * etc.</p>
 * <p>A specific handler will handle a specific node in the object(s) tree that
 * is been serialized and the handling of the next node will be passed to a 
 * child handler. By example, an handler that is defined to serialize a map will
 * have two children: one to handle the map's keys and onr to handle the map's
 * values.</p> 
 */
public interface Serialization {
	
	/**
	 * Serializes the given object
	 * @param obj The object to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException;

	/**
	 * @return The name under which the object will be serialized
	 */
	public String getName();
	
	/**
	 * @return Whether a null or empty object should be written
	 */
	public boolean isWriteEmpty();
	
}
