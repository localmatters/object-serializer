package com.localmatters.serializer.writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.serialization.Serialization;

/**
 * Interface defining a serialization writer; i.e. a class that will be 
 * responsible to produce the string representation in a specific language or 
 * form (XML, JSON, etc.) of the provided object(s).
 */
public interface Writer {

	/**
	 * Writes the root
	 * @param ser The root serialization
	 * @param root The root object (if any)
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void writeRoot(Serialization ser, 
			Object root, 
			SerializationContext ctx) throws SerializationException;

	/**
	 * Writes the given value
	 * @param ser The value serialization
	 * @param name The name under which to serialize the value
	 * @param value The value to serialize
	 * @param ctx The serialization ctx
	 * @throws SerializationException When the serialization fails
	 */
	public void writeValue(Serialization ser, 
			String name,
			Object value, 
			SerializationContext ctx) throws SerializationException;

	/**
	 * Writes a complex element
	 * @param ser The complex serialization
	 * @param name The name under which to serialize the complex element
	 * @param complex The input object
	 * @param attributes The attributes serialization
	 * @param elements The (sub-)elements serialization
	 * @param comments The comments to write
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void writeComplex(Serialization ser, 
			String name,
			Object complex, 
			Collection<Serialization> attributes, 
			Collection<Serialization> elements, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException;

	/**
	 * Writes an attribute
	 * @param ser The attribute serialization
	 * @param name The name under which to serialize the attribute
	 * @param attribute The attribute to serialize
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void writeAttribute(Serialization ser, 
			String name,
			Object attribute, 
			SerializationContext ctx) throws SerializationException;
	
	/**
	 * Writes an iterator
	 * @param ser The iterator serialization
	 * @param name The name under which to serialize the iterator
	 * @param itr The iterator to serialize
	 * @param elementName The name under which each element should be serialized
	 * @param element The elements serialization
	 * @param comments The comments to write
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void writeIterator(Serialization ser, 
			String name,
			Iterator<?> itr, 
			String elementName,
			Serialization element, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException;
	
	/**
	 * Writes a map
	 * @param ser The map serialization
	 * @param name The name under which to serialize the map
	 * @param map The map to serialize
	 * @param key The property to resolve the key
	 * @param value The values serialization
	 * @param comments The comments to write
	 * @param ctx The serialization context
	 * @throws SerializationException When the serialization fails
	 */
	public void writeMap(Serialization ser, 
			String name,
			Map<?,?> map, 
			String key,
			Serialization value, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException;
}
