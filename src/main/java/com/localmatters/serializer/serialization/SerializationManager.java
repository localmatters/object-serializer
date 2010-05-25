package com.localmatters.serializer.serialization;



/**
 * Interface describing a serialization manager; i.e. a manager that
 * holds the instance to serialize the different object(s) trees.
 */
public interface SerializationManager {

	/**
	 * Returns the serialization with the given ID
	 * @param id The id for the serialization to retrieve
	 * @return The corresponding serialization or null
	 */
	public Serialization getSerialization(String id);
}
