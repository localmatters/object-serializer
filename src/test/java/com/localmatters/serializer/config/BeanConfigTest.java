package com.localmatters.serializer.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>BeanConfig</code>
 */
public class BeanConfigTest extends TestCase {
	private BeanConfig config;
	private Config delegate;
	private Serializer serializer;
	private Object object;
	private Map<String, Object> beans;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Config.class); 
		config = new BeanConfig();
		config.setBean("listing");
		config.setDelegate(delegate);
		serializer = createMock(Serializer.class);
		object = new Object();
		beans = new HashMap<String, Object>();
		ctx = new SerializationContext(serializer, beans, null);
	}

	/**
	 * Tests handling when the bean is unknown
	 */
	public void testHandleUnknownBean() throws Exception {
		replay(delegate, serializer);
		try {
			config.handle(object, ctx);
			fail("UnknownBeanException expected");
		} catch (UnknownBeanException e) {
		}
		verify(delegate, serializer);
	}

	/**
	 * Tests handling when the bean is known
	 */
	public void testHandle() throws Exception {
		Object listing = new Object();
		beans.put("listing", listing);
		expect(delegate.handle(listing, ctx)).andReturn("<listing/>");
		replay(delegate, serializer);
		String result = config.handle(object, ctx);
		verify(delegate, serializer);
		assertEquals("<listing/>", result);
	}
}
