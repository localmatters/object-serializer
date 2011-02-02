package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
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

import org.easymock.IAnswer;

import com.localmatters.serializer.SerializationContext;
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
		ser.setComments(comments);
		writer = createMock(Writer.class);
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization when the object is not an index
	 */
	public void testHandleWhenNotIndex() throws Exception {
        expect(element.removeDefaultName()).andReturn("entry");
		replay(writer, element, parentSer);
		try {
		    ser.setElement(element);
			ser.serialize(parentSer, "orders", new Object(), ctx);
			fail("IndexExpectedException expected");
		} catch (IteratorExpectedException e) {
		}
		verify(writer, element, parentSer);
		assertEquals("entry", ser.getElementName());
	}

	/**
	 * Tests the serialization when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
        expect(element.removeDefaultName()).andReturn(null);
		writer.writeIterator(parentSer, "orders", IteratorSerialization.EMTPY_ITERATOR, null, element, comments, ctx);
		replay(writer, element, parentSer);
        ser.setElement(element);
		ser.serialize(parentSer, "orders", null, ctx);
		verify(writer, element, parentSer);
        assertNull(ser.getElementName());
	}

	/**
	 * Tests the serialization when the object is a map
	 */
	@SuppressWarnings("unchecked")
	public void testHandleMap() throws Exception {
		Map<String, String> map = createMock(Map.class);
		Set<Map.Entry<String, String>> entrySet = createMock(Set.class);
		Iterator<Map.Entry<String, String>> iterator = createMock(Iterator.class);
        expect(element.removeDefaultName()).andReturn("order");
		expect(map.entrySet()).andReturn(entrySet);
		expect(entrySet.iterator()).andReturn(iterator);
		writer.writeIterator(parentSer, "orders", iterator, "order", element, comments, ctx);
		replay(writer, element, map, entrySet, iterator);
        ser.setElement(element);
		ser.serialize(parentSer, "orders", map, ctx);
		verify(writer, element, map, entrySet, iterator);
        assertEquals("order", ser.getElementName());
	}

	/**
	 * Tests the serialization when the object is an iterable
	 */
	@SuppressWarnings("unchecked")
	public void testHandleIterable() throws Exception {
		Iterable<String> iterable = createMock(Iterable.class);
		Iterator<String> iterator = createMock(Iterator.class);
        expect(element.removeDefaultName()).andReturn("order");
		expect(iterable.iterator()).andReturn(iterator);
		writer.writeIterator(parentSer, "orders", iterator, "order", element, comments, ctx);
		replay(writer, element, iterable, iterator);
        ser.setElement(element);
		ser.serialize(parentSer, "orders", iterable, ctx);
		verify(writer, element, iterable, iterator);
	}

	/**
	 * Tests the serialization when the object is an array
	 */
	public void testHandleArray() throws Exception {
		String[] array = new String[]{"one", "two", "three"};
        expect(element.removeDefaultName()).andReturn("order");
		writer.writeIterator(same(parentSer), eq("orders"), isA(Iterator.class), eq("order"), same(element), same(comments), same(ctx));
		expectLastCall().andAnswer(new IAnswer<Object>() {
			@SuppressWarnings("unchecked")
			public Object answer() throws Throwable {
				Object[] args = getCurrentArguments();
				Iterator<String> itr = (Iterator<String>) args[2];
				assertTrue(itr.hasNext());
				assertEquals("one", itr.next());
				assertTrue(itr.hasNext());
				assertEquals("two", itr.next());
				assertTrue(itr.hasNext());
				assertEquals("three", itr.next());
				return null;
			}
		});
		replay(writer, element, parentSer);
        ser.setElement(element);
		ser.serialize(parentSer, "orders", array, ctx);
		verify(writer, element, parentSer);
	}
}
