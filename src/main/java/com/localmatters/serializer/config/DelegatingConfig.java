package com.localmatters.serializer.config;

import com.localmatters.util.StringUtils;

/**
 * Abstraction of a delegating configuration
 */
public abstract class DelegatingConfig extends AbstractConfig {
	private Config delegate;

	/**
	 * @see com.localmatters.serializer.config.Config#getName()
	 */
	@Override
	public String getName() {
		String name = super.getName();
		if (StringUtils.isBlank(name)) {
			if (getDelegate() != null) {
				return getDelegate().getName();
			}
		}
		return name;
	}
	
	/**
	 * @see com.localmatters.serializer.config.Config#isWriteEmpty()
	 */
	@Override
	public boolean isWriteEmpty() {
		Boolean writeEmpty = getWriteEmpty();
		if (writeEmpty == null) {
			if (getDelegate() != null) {
				return getDelegate().isWriteEmpty();
			}
			return true;
		}
		return writeEmpty;
	}
	
	/**
	 * @return The delegate configuration
	 */
	public Config getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate The delegate configuration
	 */
	public void setDelegate(Config delegate) {
		this.delegate = delegate;
	}
}
