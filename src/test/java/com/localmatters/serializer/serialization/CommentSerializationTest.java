package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>CommentSerialization</code>
 */
public class CommentSerializationTest extends TestCase {
	private CommentSerialization serialization;
	private Writer serializer;
	private Object object;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new CommentSerialization();
		serialization.setComment("Hello World");
		serializer = createMock(Writer.class);
		object = new Object();
	}
	
	/**
	 * Tests the serialization when not pretty
	 */
	public void testHandleWhenCommentNull() throws Exception {
		SerializationContext ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
		replay(serializer);
		String result = serialization.serialize(object, ctx);
		verify(serializer);
		assertEquals("", result);
		assertNull(serialization.getName());
		assertFalse(serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		SerializationContext ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, true);
		expect(serializer.writeComment(serialization, "Hello World", ctx)).andReturn("<-- Hello World -->");
		replay(serializer);
		String result = serialization.serialize(object, ctx);
		verify(serializer);
		assertEquals("<-- Hello World -->", result);
		assertNull(serialization.getName());
		assertFalse(serialization.isWriteEmpty());
	}
}
