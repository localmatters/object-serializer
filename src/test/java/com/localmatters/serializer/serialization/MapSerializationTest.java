package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.MapExpectedException;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>MapSerialization</code>
 */
public class MapSerializationTest extends TestCase {
	private MapSerialization serialization;
	private Serialization keySerialization;
	private Serialization valueSerialization;
	private Writer serializer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		keySerialization = createMock(Serialization.class);
		valueSerialization = createMock(Serialization.class);
		serialization = new MapSerialization();
		serialization.setName("addresses");
		serialization.setKey(keySerialization);
		serialization.setValue(valueSerialization);
		serializer = createMock(Writer.class);
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}

	/**
	 * Tests the serialization when the object is not a map
	 */
	public void testHandleWhenNotMap() throws Exception {
		replay(serializer, keySerialization, valueSerialization);
		try {
			serialization.serialize(new Object(), ctx);
			fail("MapExpectedException expected");
		} catch (MapExpectedException e) {
		}
		verify(serializer, keySerialization, valueSerialization);
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		expect(serializer.writeMap(serialization, keySerialization, valueSerialization, null, ctx.appendSegment("addresses"))).andReturn("<addresses/>");
		replay(serializer, keySerialization, valueSerialization);
		String result = serialization.serialize(null, ctx);
		verify(serializer, keySerialization, valueSerialization);
		assertEquals("<addresses/>", result);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		expect(serializer.writeMap(serialization, keySerialization, valueSerialization, map, ctx.appendSegment("addresses"))).andReturn("<addresses><home>etc.</home></addresses>");
		replay(serializer, keySerialization, valueSerialization);
		String result = serialization.serialize(map, ctx);
		verify(serializer, keySerialization, valueSerialization);
		assertEquals("<addresses><home>etc.</home></addresses>", result);
	}
}
