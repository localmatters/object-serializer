package org.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;
import org.localmatters.serializer.writer.Writer;

import junit.framework.TestCase;


/**
 * Tests the <code>ValueSerialization</code>
 */
public class ValueSerializationTest extends TestCase {
	private ValueSerialization ser;
	private Serialization parentSer;
	private Writer writer;
	private Object value;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new ValueSerialization();
		parentSer = createMock(Serialization.class);
		writer = createMock(Writer.class);
		value = new Object();
		ctx = new SerializationContext(writer, null, null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		writer.writeValue(parentSer, "name", value, ctx);
		replay(writer, parentSer);
		ser.serialize(parentSer, "name", value, ctx);
		verify(writer, parentSer);
	}
}
