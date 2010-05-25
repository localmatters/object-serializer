package com.localmatters.serializer;

/**
 * Describes an exception that is raised during the serialization
 */
public abstract class SerializationException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with the specification of the exception message
	 * @param message The exception message
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 * Constructor with the specification of the exception message and cause
	 * @param message The exception message
	 * @param cause The exception cause
	 */
	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
