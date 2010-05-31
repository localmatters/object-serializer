package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>MapSerialization</code>
 */
public class MapSerializationTest extends TestCase {
	private MapSerialization ser;
	private Serialization parentSer;
	private Serialization value;
	private List<String> comments;
	private Writer writer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		value = createMock(Serialization.class);
		parentSer = createMock(Serialization.class);
		comments = new ArrayList<String>();
		ser = new MapSerialization();
		ser.setKey("key");
		ser.setValue(value);
		ser.setComments(comments);
		writer = createMock(Writer.class);
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization when the object is not a map
	 */
	public void testHandleWhenNotMap() throws Exception {
		replay(writer, value, parentSer);
		try {
			ser.serialize(parentSer, "addresses", new Object(), ctx);
			fail("MapExpectedException expected");
		} catch (MapExpectedException e) {
		}
		verify(writer, value, parentSer);
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		writer.writeMap(parentSer, "addresses", null, "key", value, comments, ctx);
		replay(writer, value, parentSer);
		ser.serialize(parentSer, "addresses", null, ctx);
		verify(writer, value, parentSer);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		writer.writeMap(parentSer, "addresses", map, "key", value, comments, ctx);
		replay(writer, value, parentSer);
		ser.serialize(parentSer, "addresses", map, ctx);
		verify(writer, value, parentSer);
	}
}
