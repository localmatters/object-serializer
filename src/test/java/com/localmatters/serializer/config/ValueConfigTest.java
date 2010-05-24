package com.localmatters.serializer.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>ValueConfig</code>
 */
public class ValueConfigTest extends TestCase {
	private ValueConfig config;
	private Serializer serializer;
	private Object value;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		config = new ValueConfig();
		config.setName("name");
		serializer = createMock(Serializer.class);
		value = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests handling the config
	 */
	public void testHandle() throws Exception {
		expect(serializer.serializeValue(config, value, ctx.appendSegment("name"))).andReturn("<name>John Doe</name>");
		replay(serializer);
		String result = config.handle(value, ctx);
		verify(serializer);
		assertEquals("<name>John Doe</name>", result);
	}
}
