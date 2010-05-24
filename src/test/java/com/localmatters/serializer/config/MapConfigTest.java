package com.localmatters.serializer.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>MapConfig</code>
 */
public class MapConfigTest extends TestCase {
	private MapConfig config;
	private Config keyConfig;
	private Config valueConfig;
	private Serializer serializer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		keyConfig = createMock(Config.class);
		valueConfig = createMock(Config.class);
		config = new MapConfig();
		config.setName("addresses");
		config.setKeyConfig(keyConfig);
		config.setValueConfig(valueConfig);
		serializer = createMock(Serializer.class);
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}

	/**
	 * Tests handling when the object is not a map
	 */
	public void testHandleWhenNotMap() throws Exception {
		replay(serializer, keyConfig, valueConfig);
		try {
			config.handle(new Object(), ctx);
			fail("MapExpectedException expected");
		} catch (MapExpectedException e) {
		}
		verify(serializer, keyConfig, valueConfig);
	}

	/**
	 * Tests handling when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		expect(serializer.serializeMap(config, keyConfig, valueConfig, null, ctx.appendSegment("addresses"))).andReturn("<addresses/>");
		replay(serializer, keyConfig, valueConfig);
		String result = config.handle(null, ctx);
		verify(serializer, keyConfig, valueConfig);
		assertEquals("<addresses/>", result);
	}

	/**
	 * Tests handling the map
	 */
	public void testHandle() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		expect(serializer.serializeMap(config, keyConfig, valueConfig, map, ctx.appendSegment("addresses"))).andReturn("<addresses><home>etc.</home></addresses>");
		replay(serializer, keyConfig, valueConfig);
		String result = config.handle(map, ctx);
		verify(serializer, keyConfig, valueConfig);
		assertEquals("<addresses><home>etc.</home></addresses>", result);
	}
}
