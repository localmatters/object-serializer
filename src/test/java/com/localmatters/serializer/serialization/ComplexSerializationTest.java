package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>ComplexSerialization</code>
 */
public class ComplexSerializationTest extends TestCase {
	private ComplexSerialization serialization;
	private List<Serialization> attributes;
	private List<Serialization> elements;
	private Writer serializer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		attributes = new ArrayList<Serialization>();
		elements = new ArrayList<Serialization>();
		serialization = new ComplexSerialization();
		serialization.setName("listing");
		serialization.setAttributes(attributes);
		serialization.setElements(elements);
		serializer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		expect(serializer.writeComplex(serialization, attributes, elements, object, ctx.appendSegment("listing"))).andReturn("<listing/>");
		replay(serializer);
		String result = serialization.serialize(object, ctx);
		verify(serializer);
		assertEquals("<listing/>", result);
	}
}
