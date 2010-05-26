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
 * Tests the <code>ConstantSerialization</code>
 */
public class ConstantSerializationTest extends TestCase {
	private ConstantSerialization serialization;
	private Serialization delegate;
	private Writer serializer;
	private Object object;
	private Object constant;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		constant = new Object();
		serialization = new ConstantSerialization();
		serialization.setConstant(constant);
		serialization.setDelegate(delegate);
		serializer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		expect(delegate.serialize(constant, ctx)).andReturn("<type>home</type>");
		replay(delegate, serializer);
		String result = serialization.serialize(object, ctx);
		verify(delegate, serializer);
		assertEquals("<type>home</type>", result);
	}
}
