package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.UnknownBeanException;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>BeanSerialization</code>
 */
public class BeanSerializationTest extends TestCase {
	private BeanSerialization serialization;
	private Serialization delegate;
	private Writer serializer;
	private Object object;
	private Map<String, Object> beans;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		serialization = new BeanSerialization();
		serialization.setBean("listing");
		serialization.setDelegate(delegate);
		serializer = createMock(Writer.class);
		object = new Object();
		beans = new HashMap<String, Object>();
		ctx = new SerializationContext(serializer, beans, null);
	}

	/**
	 * Tests the serialization when the bean is unknown
	 */
	public void testHandleUnknownBean() throws Exception {
		replay(delegate, serializer);
		try {
			serialization.serialize(object, ctx);
			fail("UnknownBeanException expected");
		} catch (UnknownBeanException e) {
		}
		verify(delegate, serializer);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Object listing = new Object();
		beans.put("listing", listing);
		expect(delegate.serialize(listing, ctx)).andReturn("<listing/>");
		replay(delegate, serializer);
		String result = serialization.serialize(object, ctx);
		verify(delegate, serializer);
		assertEquals("<listing/>", result);
	}
}
