package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>ValueSerialization</code>
 */
public class ValueSerializationTest extends TestCase {
	private ValueSerialization serialization;
	private Writer serializer;
	private Object value;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new ValueSerialization();
		serialization.setName("name");
		serializer = createMock(Writer.class);
		value = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		expect(serializer.writeValue(serialization, value, ctx.appendSegment("name"))).andReturn("<name>John Doe</name>");
		replay(serializer);
		String result = serialization.serialize(value, ctx);
		verify(serializer);
		assertEquals("<name>John Doe</name>", result);
	}
}
