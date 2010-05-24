package com.localmatters.serializer.config;

import com.localmatters.serializer.SerializationException;


/**
 * Exception raised when a bean is unknown
 */
public class UnknownBeanException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "%s is not a valid bean";

	/**
	 * Constructor with the specification of the bean that cannot be found
	 * @param bean The bean that is unknown
	 */
	public UnknownBeanException(String bean) {
		super(String.format(MESSAGE_FORMAT, bean));
	}
}
