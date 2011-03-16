package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.easymock.IAnswer;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.MapSerialization.SameKeyValueEntry;
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
	 * Tests the serialization when the object is not a map, an array or an 
	 * iterable
	 */
	public void testHandleWhenNotMapArrayIterable() throws Exception {
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
	 * Tests the serialization of an array
	 */
	@SuppressWarnings("unchecked")
	public void testHandleArray() throws Exception {
		String[] array = new String[]{"hello", "world"};
		writer.writeMap(same(parentSer), eq("addresses"), isA(Collection.class), eq("key"), same(value), same(comments), same(ctx));
		expectLastCall().andAnswer(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				Collection<SameKeyValueEntry<String>> entries = (Collection<SameKeyValueEntry<String>>) getCurrentArguments()[2];
				assertEquals(2, CollectionUtils.size(entries));
				Iterator<SameKeyValueEntry<String>> itr = entries.iterator();
				SameKeyValueEntry<String> entry = itr.next();
				assertEquals("hello", entry.getEntry());
				assertEquals("hello", entry.getKey());
				assertEquals("hello", entry.getValue());
				entry = itr.next();
				assertEquals("world", entry.getEntry());
				assertEquals("world", entry.getKey());
				assertEquals("world", entry.getValue());
				return null;
			}
		});
		replay(writer, value, parentSer);
		ser.serialize(parentSer, "addresses", array, ctx);
		verify(writer, value, parentSer);
	}

	/**
	 * Tests the serialization of an iterable
	 */
	@SuppressWarnings("unchecked")
	public void testHandleIterable() throws Exception {
		Iterable<String> iterable = Arrays.asList("hello", "world");
		writer.writeMap(same(parentSer), eq("addresses"), isA(Collection.class), eq("key"), same(value), same(comments), same(ctx));
		expectLastCall().andAnswer(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				Collection<SameKeyValueEntry<String>> entries = (Collection<SameKeyValueEntry<String>>) getCurrentArguments()[2];
				assertEquals(2, CollectionUtils.size(entries));
				Iterator<SameKeyValueEntry<String>> itr = entries.iterator();
				SameKeyValueEntry<String> entry = itr.next();
				assertEquals("hello", entry.getEntry());
				assertEquals("hello", entry.getKey());
				assertEquals("hello", entry.getValue());
				entry = itr.next();
				assertEquals("world", entry.getEntry());
				assertEquals("world", entry.getKey());
				assertEquals("world", entry.getValue());
				return null;
			}
		});
		replay(writer, value, parentSer);
		ser.serialize(parentSer, "addresses", iterable, ctx);
		verify(writer, value, parentSer);
	}

	/**
	 * Tests the serialization of a map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testHandle() throws Exception {
		Map map = new HashMap<String, String>();
		writer.writeMap(parentSer, "addresses", map.entrySet(), "key", value, comments, ctx);
		replay(writer, value, parentSer);
		ser.serialize(parentSer, "addresses", map, ctx);
		verify(writer, value, parentSer);
	}
}
