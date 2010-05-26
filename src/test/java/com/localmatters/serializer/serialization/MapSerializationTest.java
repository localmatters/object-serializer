package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Serialization key;
	private Serialization value;
	private List<String> comments;
	private Writer serializer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		key = createMock(Serialization.class);
		value = createMock(Serialization.class);
		comments = new ArrayList<String>();
		serialization = new MapSerialization();
		serialization.setName("addresses");
		serialization.setKey(key);
		serialization.setValue(value);
		serialization.setComments(comments);
		serializer = createMock(Writer.class);
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
	}

	/**
	 * Tests the serialization when the object is not a map
	 */
	public void testHandleWhenNotMap() throws Exception {
		replay(serializer, key, value);
		try {
			serialization.serialize(new Object(), ctx);
			fail("MapExpectedException expected");
		} catch (MapExpectedException e) {
		}
		verify(serializer, key, value);
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		expect(serializer.writeMap(serialization, comments, key, value, null, ctx.appendSegment("addresses"))).andReturn("<addresses/>");
		replay(serializer, key, value);
		String result = serialization.serialize(null, ctx);
		verify(serializer, key, value);
		assertEquals("<addresses/>", result);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		expect(serializer.writeMap(serialization, comments, key, value, map, ctx.appendSegment("addresses"))).andReturn("<addresses><home>etc.</home></addresses>");
		replay(serializer, key, value);
		String result = serialization.serialize(map, ctx);
		verify(serializer, key, value);
		assertEquals("<addresses><home>etc.</home></addresses>", result);
	}
}
