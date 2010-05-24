package com.localmatters.serializer.resolver;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class defines the abstraction of a delegating resolver.
 */
public abstract class DelegatingPropertyResolver implements PropertyResolver {
	private PropertyResolver delegate;

	/**
	 * @return The delegate resolver
	 */
	public PropertyResolver getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate The delegate resolver
	 */
	@Required
	public void setDelegate(PropertyResolver delegate) {
		this.delegate = delegate;
	}
}
