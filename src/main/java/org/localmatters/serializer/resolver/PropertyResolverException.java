package org.localmatters.serializer.resolver;

/**
 * Describes an exception in the property resolution
 */
public abstract class PropertyResolverException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with the specification of the exception message
	 * @param message The exception message
	 */
	public PropertyResolverException(String message) {
		super(message);
	}
}
