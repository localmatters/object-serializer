package com.localmatters.serializer.resolver;

/**
 * Exception when the property is not public (i.e. either invalid or private)
 */
public class NotPublicPropertyException extends PropertyResolverException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "The property %s is invalid or private for the type %s";

	/**
	 * Default constructor with the specification of the invalid or private
	 * property and the type 
	 * @param property The invalidor private property
	 * @param type The type of the object for which this property is invalid
	 */
	public NotPublicPropertyException(String property, String type) {
		super(String.format(MESSAGE_FORMAT, property, type));
	}
}
