package com.localmatters.serializer.config;

/**
 * Describes an exception that is due to an error in the configuration
 */
public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with the specification of the exception message
	 * @param message The exception message
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor with the specification of the exception message format
	 * and its arguments
	 * @param format The exception message format
	 * @param args The message arguments
	 */
	public ConfigurationException(String format, Object...args) {
		super(String.format(format, args));
	}

	/**
	 * Constructor with the specification of the exception message and cause
	 * @param message The exception message
	 * @param cause The exception cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
