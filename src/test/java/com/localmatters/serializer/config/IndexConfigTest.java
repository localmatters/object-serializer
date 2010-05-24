package com.localmatters.serializer.config;

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
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>IndexConfig</code>
 */
public class IndexConfigTest extends TestCase {
	private IndexConfig config;
	private Config elementConfig;
	private Serializer serializer;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		elementConfig = createMock(Config.class);
		config = new IndexConfig();
		config.setName("orders");
		config.setElementConfig(elementConfig);
		serializer = createMock(Serializer.class);
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}

	/**
	 * Tests handling when the object is not an index
	 */
	public void testHandleWhenNotIndex() throws Exception {
		replay(serializer, elementConfig);
		try {
			config.handle(new Object(), ctx);
			fail("IndexExpectedException expected");
		} catch (IndexExpectedException e) {
		}
		verify(serializer, elementConfig);
	}

	/**
	 * Tests handling when the object is null
	 */
	public void testHandleWhenNull() throws Exception {
		expect(serializer.serializeIndex(config, elementConfig, IndexConfig.EMTPY_ITERATOR, ctx.appendSegment("orders"))).andReturn("<orders/>");
		replay(serializer, elementConfig);
		String result = config.handle(null, ctx);
		verify(serializer, elementConfig);
		assertEquals("<orders/>", result);
	}

	/**
	 * Tests handling a map
	 */
	@SuppressWarnings("unchecked")
	public void testHandleMap() throws Exception {
		Map<String, String> map = createMock(Map.class);
		Set<Map.Entry<String, String>> entrySet = createMock(Set.class);
		Iterator<Map.Entry<String, String>> iterator = createMock(Iterator.class);
		expect(map.entrySet()).andReturn(entrySet);
		expect(entrySet.iterator()).andReturn(iterator);
		expect(serializer.serializeIndex(config, elementConfig, iterator, ctx.appendSegment("orders"))).andReturn("<orders><entry><key/><value/></entry></orders>");
		replay(serializer, elementConfig, map, entrySet, iterator);
		String result = config.handle(map, ctx);
		verify(serializer, elementConfig, map, entrySet, iterator);
		assertEquals("<orders><entry><key/><value/></entry></orders>", result);
	}

	/**
	 * Tests handling an iterable
	 */
	@SuppressWarnings("unchecked")
	public void testHandleIterable() throws Exception {
		Iterable<String> iterable = createMock(Iterable.class);
		Iterator<String> iterator = createMock(Iterator.class);
		expect(iterable.iterator()).andReturn(iterator);
		expect(serializer.serializeIndex(config, elementConfig, iterator, ctx.appendSegment("orders"))).andReturn("<orders><order/></orders>");
		replay(serializer, elementConfig, iterable, iterator);
		String result = config.handle(iterable, ctx);
		verify(serializer, elementConfig, iterable, iterator);
		assertEquals("<orders><order/></orders>", result);
	}

	/**
	 * Tests handling an array
	 */
	public void testHandleArray() throws Exception {
		String[] array = new String[]{"one", "two", "three"};
		expect(serializer.serializeIndex(same(config), same(elementConfig), isA(Iterator.class), eq(ctx.appendSegment("orders")))).andReturn("<orders><order/></orders>");
		replay(serializer, elementConfig);
		String result = config.handle(array, ctx);
		verify(serializer, elementConfig);
		assertEquals("<orders><order/></orders>", result);
	}
}
