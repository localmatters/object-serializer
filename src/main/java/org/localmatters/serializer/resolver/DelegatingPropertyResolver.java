package org.localmatters.serializer.resolver;



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
	public void setDelegate(PropertyResolver delegate) {
		this.delegate = delegate;
	}
}
