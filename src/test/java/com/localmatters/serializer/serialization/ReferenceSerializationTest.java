package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>ReferenceSerialization</code>
 */
public class ReferenceSerializationTest extends TestCase {
	private ReferenceSerialization ser;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new ReferenceSerialization();
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Serialization parentSer = createMock(Serialization.class);
		Serialization referenced = createMock(Serialization.class); 
		Writer writer = createMock(Writer.class);
		SerializationContext ctx = new SerializationContext(writer, null, null);
		Object object = new Object();
		ser.setReferenced(referenced);
		referenced.serialize(parentSer, "name", object, ctx);
		replay(referenced, writer, parentSer);
		ser.serialize(parentSer, "name", object, ctx);
		verify(referenced, writer, parentSer);
	}
	
	/**
	 * Tests getting the write empty when it and delegate are null
	 */
	public void testIsWriteEmptyWhenNullAndNoDelegate() throws Exception {
		assertTrue(ser.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is null, but not the delegate
	 */
	public void testIsWriteEmptyWhenNullAndDelegate() throws Exception {
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		ser.setReferenced(referenced);
		assertFalse(ser.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is not null
	 */
	public void testIsWriteEmptyWhenNotNull() throws Exception {
		ser.setWriteEmpty(Boolean.TRUE);
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		ser.setReferenced(referenced);
		assertTrue(ser.isWriteEmpty());
	}
	
	
}
