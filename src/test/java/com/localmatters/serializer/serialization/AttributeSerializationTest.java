package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>AttributeSerialization</code>
 */
public class AttributeSerializationTest extends TestCase {
	private AttributeSerialization serialization;
	private Writer serializer;
	private Object attribute;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new AttributeSerialization();
		serialization.setName("name");
		serializer = createMock(Writer.class);
		attribute = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		expect(serializer.writeAttribute(serialization, attribute, ctx.appendSegment("name"))).andReturn(" name=\"John Doe\"");
		replay(serializer);
		String result = serialization.serialize(attribute, ctx);
		verify(serializer);
		assertEquals(" name=\"John Doe\"", result);
	}
}
