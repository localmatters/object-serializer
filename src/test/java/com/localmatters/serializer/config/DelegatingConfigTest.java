package com.localmatters.serializer.config;

import junit.framework.TestCase;

/**
 * Tests the <code>DelegatingConfig</code>
 */
public class DelegatingConfigTest extends TestCase {
	private ReferenceConfig config;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		config = new ReferenceConfig();
	}
	
	/**
	 * Tests the get name when the name and delegate are null
	 */
	public void testGetNameWhenNullAndNoDelegate() throws Exception {
		assertNull(config.getName());
	}
	
	/**
	 * Tests the get name when the name is null, but not the delegate
	 */
	public void testGetNameWhenNullAndDelegate() throws Exception {
		ValueConfig delegate = new ValueConfig();
		delegate.setName("delegate");
		config.setDelegate(delegate);
		assertEquals("delegate", config.getName());
	}
	
	/**
	 * Tests the get name when the name is not null
	 */
	public void testGetNameWhenNotNull() throws Exception {
		config.setName("name");
		ValueConfig delegate = new ValueConfig();
		delegate.setName("delegate");
		config.setDelegate(delegate);
		assertEquals("name", config.getName());
	}
	
	/**
	 * Tests the is write empty when the write empty and delegate are null
	 */
	public void testIsWriteEmptyWhenNullAndNoDelegate() throws Exception {
		assertTrue(config.isWriteEmpty());
	}
	
	/**
	 * Tests the is write empty when the write empty is null, but not the
	 * delegate
	 */
	public void testIsWriteEmptyWhenNullAndDelegate() throws Exception {
		ValueConfig delegate = new ValueConfig();
		delegate.setWriteEmpty(Boolean.FALSE);
		config.setDelegate(delegate);
		assertFalse(config.isWriteEmpty());
	}
	
	/**
	 * Tests the is write empty when the write empty is not null
	 */
	public void testIsWriteEmptyWhenNotNull() throws Exception {
		config.setWriteEmpty(Boolean.TRUE);
		ValueConfig delegate = new ValueConfig();
		delegate.setWriteEmpty(Boolean.FALSE);
		config.setDelegate(delegate);
		assertTrue(config.isWriteEmpty());
	}
}
