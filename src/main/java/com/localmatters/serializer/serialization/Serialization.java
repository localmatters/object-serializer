package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * <p>Classes that will handle the serialization of a given object, property, 
 * etc.</p>
 * <p>A specific serialization will handle a specific node in the object(s) tree
 * that is been serialized and the handling of the next node will be passed to a 
 * child handler. By example, an handler that is defined to serialize an 
 * iterable will have a child to handle each of the iterable items.</p> 
 */
public interface Serialization {
	
	/**
	 * Serializes the given object
	 * @param ser The serialization context
	 * @param name The name under which to serialize the object
	 * @param obj The object to serialize
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException;
	
	/**
	 * @return Whether a null or empty object should be written
	 */
	public boolean isWriteEmpty();
	
	/**
	 * Serialization uses the delegation pattern to attach a context in which it
	 * applies. Such context can consist of the name under which the 
	 * serialization will write an object, the bean that represents this object
	 * and/or the property of this bean that represents it. Sometimes, it is 
	 * useful to get access to the serialization without its context to, by 
	 * example, re-use it in other context 
	 * @return The serialization without its context
	 */
	public Serialization getContextlessSerialization();
	
}
