package org.localmatters.serializer.serialization;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.serialization.AbstractSerialization;
import org.localmatters.serializer.serialization.Serialization;

import junit.framework.TestCase;

/**
 * Tests the <code>AbstractSerialization</code>
 */
public class AbstractSerializationTest extends TestCase {
	private AbstractSerialization serialization;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new AbstractSerialization() {
			public void serialize(Serialization ser, String name, Object obj,
					SerializationContext ctx) throws SerializationException {
			}
		};
	}

	/**
	 * Tests the is write empty method
	 */
	public void testIsWriteEmpty() {
		serialization.setWriteEmpty(null);
		assertFalse(serialization.isWriteEmpty());
		assertNull(serialization.getWriteEmpty());
		serialization.setWriteEmpty(Boolean.TRUE);
		assertTrue(serialization.isWriteEmpty());
		assertSame(Boolean.TRUE, serialization.getWriteEmpty());
		serialization.setWriteEmpty(Boolean.FALSE);
		assertFalse(serialization.isWriteEmpty());
		assertSame(Boolean.FALSE, serialization.getWriteEmpty());
	}
	
	/**
	 * Tests the get context less serialization
	 */
	public void testGetContextlessSerialization() {
		assertSame(serialization, serialization.getContextlessSerialization());
	}
}
