package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.util.SerializationUtils;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>IteratorSerialization</code>
 */
public class IteratorSerializationTest extends TestCase {
	private IteratorSerialization ser;
	private Serialization parentSer;
	private Serialization element;
	private List<String> comments;
	private Writer writer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		element = createMock(Serialization.class);
		parentSer = createMock(Serialization.class);
		comments = new ArrayList<String>();
		ser = new IteratorSerialization();
		ser.setElementName("order");
		ser.setElement(element);
		ser.setComments(comments);
		writer = createMock(Writer.class);
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization when the object is not an index
	 */
	public void testHandleWhenNotIndex() throws Exception {
		replay(writer, element, parentSer);
		try {
			ser.serialize(parentSer, "orders", new Object(), ctx);
			fail("IndexExpectedException expected");
		} catch (IteratorExpectedException e) {
		}
		verify(writer, element, parentSer);
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		writer.writeIterator(parentSer, "orders", IteratorSerialization.EMTPY_ITERATOR, "order", element, comments, ctx);
		replay(writer, element, parentSer);
		ser.serialize(parentSer, "orders", null, ctx);
		verify(writer, element, parentSer);
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
		writer.writeIterator(parentSer, "orders", iterator, "order", element, comments, ctx);
		replay(writer, element, map, entrySet, iterator);
		ser.serialize(parentSer, "orders", map, ctx);
		verify(writer, element, map, entrySet, iterator);
	}

	/**
	 * Tests the serialization when the object is an iterable
	 */
	@SuppressWarnings("unchecked")
	public void testHandleIterable() throws Exception {
		Iterable<String> iterable = createMock(Iterable.class);
		Iterator<String> iterator = createMock(Iterator.class);
		expect(iterable.iterator()).andReturn(iterator);
		writer.writeIterator(parentSer, "orders", iterator, "order", element, comments, ctx);
		replay(writer, element, iterable, iterator);
		ser.serialize(parentSer, "orders", iterable, ctx);
		verify(writer, element, iterable, iterator);
	}

	/**
	 * Tests the serialization when the object is an array
	 */
	public void testHandleArray() throws Exception {
		String[] array = new String[]{"one", "two", "three"};
		writer.writeIterator(same(parentSer), eq("orders"), isA(Iterator.class), eq("order"), same(element), same(comments), same(ctx));
		replay(writer, element, parentSer);
		ser.serialize(parentSer, "orders", array, ctx);
		verify(writer, element, parentSer);
	}
	
	public void testSetNameDelegate() {
		NameSerialization name = SerializationUtils.createName("entry", element);
		replay(writer, element, parentSer);
		ser.setElement(name);
		verify(writer, element, parentSer);
		assertSame(element, ser.getElement());
		assertEquals("entry", ser.getElementName());
		
	}
}
