package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Exception raised when a class is not an expected Iterable or Array
 */
public class IndexExpectedException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "%s is not an indexable (iterable, array or map) object, but is defined as one in the configuration!";

	/**
	 * Constructor with the specification of the bean's path that is not a list
	 * @param context The serialization context
	 */
	public IndexExpectedException(SerializationContext context) {
		super(String.format(MESSAGE_FORMAT, context.getPath()));
	}
}
