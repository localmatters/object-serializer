package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

/**
 * Exception raised when a class is not an expected Iterable, Array or a map
 */
public class IteratorExpectedException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "%s is not an object that can be iterate over (Iterable, Array or Map), but is defined as one in the configuration!";

	/**
	 * Constructor with the specification of the context
	 * @param context The serialization context
	 */
	public IteratorExpectedException(SerializationContext context) {
		super(String.format(MESSAGE_FORMAT, context.getPath()));
	}
}
