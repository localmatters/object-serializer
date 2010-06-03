package com.localmatters.serializer.serialization;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Exception raised when the serialization name is required, but missing
 */
public class NameExpectedException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "The serialization of %s requires a name, but none has been specified either by the name or property attribute in the config!";

	/**
	 * Constructor with the specification of the context
	 * @param context The serialization context
	 */
	public NameExpectedException(SerializationContext context) {
		super(String.format(MESSAGE_FORMAT, context.getPath()));
	}
}
