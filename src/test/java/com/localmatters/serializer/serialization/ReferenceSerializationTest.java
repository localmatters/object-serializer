package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>ReferenceSerialization</code>
 */
public class ReferenceSerializationTest extends TestCase {
	private ReferenceSerialization serialization;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new ReferenceSerialization();
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Serialization referenced = createMock(Serialization.class); 
		Writer serializer = createMock(Writer.class);
		SerializationContext ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
		Object object = new Object();
		serialization.setReferenced(referenced);
		expect(referenced.serialize(object, ctx)).andReturn("<name>John Doe</name>");
		replay(referenced, serializer);
		String result = serialization.serialize(object, ctx);
		verify(referenced, serializer);
		assertEquals("<name>John Doe</name>", result);
	}
	
	/**
	 * Tests getting the name when it and the delegate are null
	 */
	public void testGetNameWhenNullAndNoDelegate() throws Exception {
		assertNull(serialization.getName());
	}
	
	/**
	 * Tests getting the name when it is null, but not the delegate
	 */
	public void testGetNameWhenNullAndDelegate() throws Exception {
		ValueSerialization referenced = new ValueSerialization();
		referenced.setName("referenced");
		serialization.setReferenced(referenced);
		assertEquals("referenced", serialization.getName());
	}
	
	/**
	 * Tests getting the name when it is not null
	 */
	public void testGetNameWhenNotNull() throws Exception {
		serialization.setName("name");
		ValueSerialization referenced = new ValueSerialization();
		referenced.setName("referenced");
		serialization.setReferenced(referenced);
		assertEquals("name", serialization.getName());
	}
	
	/**
	 * Tests getting the write empty when it and delegate are null
	 */
	public void testIsWriteEmptyWhenNullAndNoDelegate() throws Exception {
		assertTrue(serialization.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is null, but not the delegate
	 */
	public void testIsWriteEmptyWhenNullAndDelegate() throws Exception {
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		serialization.setReferenced(referenced);
		assertFalse(serialization.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is not null
	 */
	public void testIsWriteEmptyWhenNotNull() throws Exception {
		serialization.setWriteEmpty(Boolean.TRUE);
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		serialization.setReferenced(referenced);
		assertTrue(serialization.isWriteEmpty());
	}
	
	
}
