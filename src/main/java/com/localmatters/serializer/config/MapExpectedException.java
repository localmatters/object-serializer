package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Exception raised when a class is not an expected map
 */
public class MapExpectedException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "%s is not a map, but is defined as one in the configuration!";

	/**
	 * Constructor with the specification of the bean's path that is not a map
	 * @param context The serialization context
	 */
	public MapExpectedException(SerializationContext context) {
		super(String.format(MESSAGE_FORMAT, context.getPath()));
	}
}
