package org.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.serialization.AttributeSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.writer.Writer;

import junit.framework.TestCase;


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
