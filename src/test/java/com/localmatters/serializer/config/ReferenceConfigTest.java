package com.localmatters.serializer.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>ReferenceConfig</code>
 */
public class ReferenceConfigTest extends TestCase {
	private ReferenceConfig config;
	private Config delegate;
	private Serializer serializer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Config.class); 
		config = new ReferenceConfig();
		config.setDelegate(delegate);
		serializer = createMock(Serializer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests handling when the property is valid
	 */
	public void testHandle() throws Exception {
		expect(delegate.handle(object, ctx)).andReturn("<name>John Doe</name>");
		replay(delegate, serializer);
		String result = config.handle(object, ctx);
		verify(delegate, serializer);
		assertEquals("<name>John Doe</name>", result);
	}
}
