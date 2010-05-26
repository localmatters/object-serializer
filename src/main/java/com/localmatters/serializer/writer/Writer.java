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
	 * @param serialization The root serialization
	 * @param root The root object (if any)
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeRoot(Serialization serialization, 
			Object root, 
			SerializationContext context) throws SerializationException;

	/**
	 * Writes the given value
	 * @param serialization The value serialization
	 * @param value The value to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeValue(Serialization serialization, 
			Object value, 
			SerializationContext context) throws SerializationException;

	/**
	 * Writes a complex element
	 * @param serialization The complex serialization
	 * @param comments The comments to write
	 * @param attributeHandlers The attributes serialization handlers
	 * @param elementHandlers The (sub-)elements serialization handlers
	 * @param complex The input object
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeComplex(Serialization serialization, 
			Collection<String> comments, 
			Collection<Serialization> attributeHandlers, 
			Collection<Serialization> elementHandlers, 
			Object complex, 
			SerializationContext context) throws SerializationException;

	/**
	 * Writes an attribute
	 * @param handler The attribute serialization
	 * @param attribute The attribute to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeAttribute(Serialization handler, 
			Object attribute, 
			SerializationContext context) throws SerializationException;
	
	/**
	 * Writes an iterator
	 * @param serialization The iterator serialization
	 * @param comments The comments to write
	 * @param elementHandler The elements serialization handler
	 * @param iterator The iterator to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeIterator(Serialization serialization, 
			Collection<String> comments, 
			Serialization elementHandler, 
			Iterator<?> iterator, 
			SerializationContext context) throws SerializationException;
	
	/**
	 * Writes a map
	 * @param serialization The map serialization
	 * @param comments The comments to write
	 * @param keyHandler The keys serialization handler
	 * @param valuesHandler The values serialization handler
	 * @param map The map to serialize
	 * @param context The serialization context
	 * @return The resulting string
	 * @throws SerializationException When the serialization fails
	 */
	public String writeMap(Serialization serialization, 
			Collection<String> comments, 
			Serialization keyHandler, 
			Serialization valueHandler, 
			Map<?,?> map, 
			SerializationContext context) throws SerializationException;
}
