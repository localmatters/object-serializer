package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;


/**
 * Exception raised when the serialization IO failed
 */
public class IOSerializationException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "Exception encountered when serializing %s";

	/**
	 * Constructor with the specification of the context and cause exception
	 * @param property The property that could not be resolved
	 * @param cause The cause for this exception
	 */
	public IOSerializationException(SerializationContext context, Throwable cause) {
		super(String.format(MESSAGE_FORMAT, context.getPath()), cause);
	}
}
