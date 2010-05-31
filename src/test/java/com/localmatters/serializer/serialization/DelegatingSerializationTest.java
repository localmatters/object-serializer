package com.localmatters.serializer.serialization;

import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.ValueSerialization;

import junit.framework.TestCase;

/**
 * Tests the <code>DelegatingSerialization</code>
 */
public class DelegatingSerializationTest extends TestCase {
	private DelegatingSerialization serialization;
	private ValueSerialization delegate;

	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = new ValueSerialization();
		serialization = new BeanSerialization();
		serialization.setDelegate(delegate);
	}
	
	/**
	 * Tests getting the write empty
	 */
	public void testIsWriteEmpty() throws Exception {
		assertTrue(serialization.isWriteEmpty());
		delegate.setWriteEmpty(Boolean.FALSE);
		assertFalse(serialization.isWriteEmpty());
	}
}
