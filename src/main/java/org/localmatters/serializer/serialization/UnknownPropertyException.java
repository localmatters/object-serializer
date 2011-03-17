package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;


/**
 * Exception raised when a property could not be resolved for a bean
 */
public class UnknownPropertyException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "%s is not a valid/accessible property for the object at %s";

	/**
	 * Constructor with the specification of the property, context and cause 
	 * exception
	 * @param property The property that could not be resolved
	 * @param context The serialization context
	 * @param cause The cause for this exception
	 */
	public UnknownPropertyException(String property, SerializationContext context, Throwable cause) {
		super(String.format(MESSAGE_FORMAT, property, context.getPath()), cause);
	}
}
