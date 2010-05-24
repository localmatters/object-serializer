package com.localmatters.serializer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.localmatters.serializer.config.Config;



public interface Serializer {

	/**
	 * Serializes the given root
	 * @param config The serialization configuration
	 * @param root The root object
	 * @param context The serialization context
	 * @return The serialized root
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeRoot(Config config, Object root, SerializationContext context) throws SerializationException;

	/**
	 * Serializes the given value
	 * @param config The serialization configuration
	 * @param value The value to serialize
	 * @param context The serialization context
	 * @return The serialized value
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeValue(Config config, Object value, SerializationContext context) throws SerializationException;

	/**
	 * Serializes a complex element
	 * @param config The serialization configuration
	 * @param attributeConfigs The configuration of the attributes
	 * @param elementConfigs The configuration of the (sub-)elements
	 * @param object The input object
	 * @param context The serialization context
	 * @return The serialized bean
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeComplex(Config config, Collection<Config> attributeConfigs, Collection<Config> elementConfigs, Object object, SerializationContext context) throws SerializationException;

	/**
	 * Serializes the given bean's attribute
	 * @param config The serialization configuration
	 * @param attribute The bean's attribute to serialize
	 * @param context The serialization context
	 * @return The serialized bean's attribute
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeAttribute(Config config, Object attribute, SerializationContext context) throws SerializationException;
	
	/**
	 * Serializes an index (list, array) specified by the given iterator
	 * @param config The serialization configuration
	 * @param elementConfig The configuration of the serialization of the index
	 * elements
	 * @param index The iterator on the elements to serialize
	 * @param context The serialization context
	 * @return The serialized index
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeIndex(Config config, Config elementConfig, Iterator<?> index, SerializationContext context) throws SerializationException;
	
	/**
	 * Serializes a map
	 * @param config The serialization configuration
	 * @param keyConfig The configuration of the serialization of the keys
	 * @param valuesConfig The configuration of the serialization of the values
	 * @param map The map to serialize
	 * @param context The serialization context
	 * @return The serialized map
	 * @throws SerializationException When an error is found in the config
	 */
	public String serializeMap(Config config, Config keyConfig, Config valueConfig, Map<?,?> map, SerializationContext context) throws SerializationException;
}
