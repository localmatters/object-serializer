package org.localmatters.serializer.serialization;


/**
 * Abstraction of a serialization that delegates the object serialization to a
 * delegate. This enables adding some additional function to a serialization
 * without having to override, modify it. 
 */
public abstract class DelegatingSerialization implements Serialization {
	private Serialization delegate;
	
	/**
	 * @see org.localmatters.serializer.serialization.Serialization#isWriteEmpty()
	 */
	public boolean isWriteEmpty() {
		return getDelegate().isWriteEmpty();
	}
	
	/**
	 * @return The delegate configuration
	 */
	public Serialization getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate The delegate configuration
	 */
	public void setDelegate(Serialization delegate) {
		this.delegate = delegate;
	}

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#getContextlessSerialization()
	 */
	public Serialization getContextlessSerialization() {
		return getDelegate().getContextlessSerialization();
	}

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#removeDefaultName()
	 */
	public String removeDefaultName() {
	    return getDelegate().removeDefaultName();
	}
}
