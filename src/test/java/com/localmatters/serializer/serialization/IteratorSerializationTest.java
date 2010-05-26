package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.IteratorExpectedException;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>IteratorSerialization</code>
 */
public class IteratorSerializationTest extends TestCase {
	private IteratorSerialization serialization;
	private Serialization elementSerialization;
	private Writer serializer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		elementSerialization = createMock(Serialization.class);
		serialization = new IteratorSerialization();
		serialization.setName("orders");
		serialization.setElement(elementSerialization);
		serializer = createMock(Writer.class);
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null, false);
	}

	/**
	 * Tests the serialization when the object is not an index
	 */
	public void testHandleWhenNotIndex() throws Exception {
		replay(serializer, elementSerialization);
		try {
			serialization.serialize(new Object(), ctx);
			fail("IndexExpectedException expected");
		} catch (IteratorExpectedException e) {
		}
		verify(serializer, elementSerialization);
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		expect(serializer.writeIterator(serialization, elementSerialization, IteratorSerialization.EMTPY_ITERATOR, ctx.appendSegment("orders"))).andReturn("<orders/>");
		replay(serializer, elementSerialization);
		String result = serialization.serialize(null, ctx);
		verify(serializer, elementSerialization);
		assertEquals("<orders/>", result);
	}

	/**
	 * Tests the serialization when the object is a map
	 */
	@SuppressWarnings("unchecked")
	public void testHandleMap() throws Exception {
		Map<String, String> map = createMock(Map.class);
		Set<Map.Entry<String, String>> entrySet = createMock(Set.class);
		Iterator<Map.Entry<String, String>> iterator = createMock(Iterator.class);
		expect(map.entrySet()).andReturn(entrySet);
		expect(entrySet.iterator()).andReturn(iterator);
		expect(serializer.writeIterator(serialization, elementSerialization, iterator, ctx.appendSegment("orders"))).andReturn("<orders><entry><key/><value/></entry></orders>");
		replay(serializer, elementSerialization, map, entrySet, iterator);
		String result = serialization.serialize(map, ctx);
		verify(serializer, elementSerialization, map, entrySet, iterator);
		assertEquals("<orders><entry><key/><value/></entry></orders>", result);
	}

	/**
	 * Tests the serialization when the object is an iterable
	 */
	@SuppressWarnings("unchecked")
	public void testHandleIterable() throws Exception {
		Iterable<String> iterable = createMock(Iterable.class);
		Iterator<String> iterator = createMock(Iterator.class);
		expect(iterable.iterator()).andReturn(iterator);
		expect(serializer.writeIterator(serialization, elementSerialization, iterator, ctx.appendSegment("orders"))).andReturn("<orders><order/></orders>");
		replay(serializer, elementSerialization, iterable, iterator);
		String result = serialization.serialize(iterable, ctx);
		verify(serializer, elementSerialization, iterable, iterator);
		assertEquals("<orders><order/></orders>", result);
	}

	/**
	 * Tests the serialization when the object is an array
	 */
	public void testHandleArray() throws Exception {
		String[] array = new String[]{"one", "two", "three"};
		expect(serializer.writeIterator(same(serialization), same(elementSerialization), isA(Iterator.class), eq(ctx.appendSegment("orders")))).andReturn("<orders><order/></orders>");
		replay(serializer, elementSerialization);
		String result = serialization.serialize(array, ctx);
		verify(serializer, elementSerialization);
		assertEquals("<orders><order/></orders>", result);
	}
}
