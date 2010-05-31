package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>AttributeSerialization</code>
 */
public class AttributeSerializationTest extends TestCase {
	private AttributeSerialization ser;
	private Serialization parentSer;
	private Writer writer;
	private Object attribute;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new AttributeSerialization();
		parentSer = createMock(Serialization.class);
		writer = createMock(Writer.class);
		attribute = new Object();
		ctx = new SerializationContext(writer, null, null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		writer.writeAttribute(parentSer, "name", attribute, ctx);
		replay(writer);
		ser.serialize(parentSer, "name", attribute, ctx);
		verify(writer);
	}
}
