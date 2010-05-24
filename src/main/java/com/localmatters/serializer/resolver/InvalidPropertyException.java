package com.localmatters.serializer.resolver;

/**
 * Exception when the property is invalid
 */
public class InvalidPropertyException extends PropertyResolverException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "The property %s is invalid for the type %s";

	/**
	 * Default constructor with the specification of the invalid property and
	 * the type 
	 * @param property The invalid property
	 * @param type The type of the object for which this property is invalid
	 */
	public InvalidPropertyException(String property, String type) {
		super(String.format(MESSAGE_FORMAT, property, type));
	}
}
