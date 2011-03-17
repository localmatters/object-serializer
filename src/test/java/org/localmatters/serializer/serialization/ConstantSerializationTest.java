package org.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.serialization.ConstantSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.writer.Writer;

import junit.framework.TestCase;


/**
 * Tests the <code>ConstantSerialization</code>
 */
public class ConstantSerializationTest extends TestCase {
	private ConstantSerialization ser;
	private Serialization parentSer;
	private Serialization delegate;
	private Writer writer;
	private Object object;
	private Object constant;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		parentSer = createMock(Serialization.class);
		delegate = createMock(Serialization.class); 
		constant = new Object();
		ser = new ConstantSerialization();
		ser.setConstant(constant);
		ser.setDelegate(delegate);
		writer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		delegate.serialize(parentSer, "name", constant, ctx);
		replay(delegate, writer);
		ser.serialize(parentSer, "name", object, ctx);
		verify(delegate, writer);
	}
}
