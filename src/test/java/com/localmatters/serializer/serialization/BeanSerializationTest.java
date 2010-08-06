package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>BeanSerialization</code>
 */
public class BeanSerializationTest extends TestCase {
	private BeanSerialization ser;
	private Serialization parentSer;
	private Serialization delegate;
	private Writer writer;
	private Object object;
	private Map<String, Object> beans;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		parentSer = createMock(Serialization.class);
		ser = new BeanSerialization();
		ser.setBean("listing");
		ser.setDelegate(delegate);
		writer = createMock(Writer.class);
		object = new Object();
		beans = new HashMap<String, Object>();
		ctx = new SerializationContext(writer, null, null, beans);
	}

	/**
	 * Tests the serialization when the bean is unknown and not write empty
	 */
	public void testHandleUnknownBeanNotWriteEmpty() throws Exception {
		expect(delegate.isWriteEmpty()).andReturn(false);
		replay(delegate, writer);
		ser.serialize(parentSer, "bean", object, ctx);
		verify(delegate, writer);
	}

	/**
	 * Tests the serialization when the bean is unknown and write empty
	 */
	public void testHandleUnknownBeanWriteEmpty() throws Exception {
		expect(delegate.isWriteEmpty()).andReturn(true);
		delegate.serialize(parentSer, "bean", null, ctx);
		replay(delegate, writer);
		ser.serialize(parentSer, "bean", object, ctx);
		verify(delegate, writer);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Object listing = new Object();
		beans.put("listing", listing);
		delegate.serialize(parentSer, "bean", listing, ctx);
		replay(delegate, writer);
		ser.serialize(parentSer, "bean", object, ctx);
		verify(delegate, writer);
	}
}
