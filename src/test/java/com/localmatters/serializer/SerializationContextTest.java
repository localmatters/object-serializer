/**
 * 
 */
package com.localmatters.serializer;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import com.localmatters.serializer.resolver.PropertyResolver;

import junit.framework.TestCase;

/**
 * Tests the <code>SerializationContext</code>
 */
public class SerializationContextTest extends TestCase {
	private SerializationContext context;
	private Serializer serializer;
	private PropertyResolver propertyResolver;
	private Map<String, Object> beans;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serializer = createMock(Serializer.class);
		propertyResolver = createMock(PropertyResolver.class);
		beans = new HashMap<String, Object>();
		context = new SerializationContext(serializer, beans, propertyResolver);
	}

	/**
	 * Tests the append methods
	 */
	public void testAppend() {
		replay(serializer, propertyResolver);
		assertEquals(context, context.appendSegment(""));
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "listing"), context.appendSegment("listing"));
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "listing.name"), context.appendSegment("listing").appendSegment("name"));
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "listing.addresses{}"), context.appendSegment("listing").appendSegment("addresses").appendMap());
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "listing.addresses{home}"), context.appendSegment("listing").appendSegment("addresses").appendMap("home"));
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "listing.orders[1]"), context.appendSegment("listing").appendSegment("orders").appendIndex(1));
		assertEquals(new SerializationContext(serializer, beans, propertyResolver, "addresses[1].key"), context.appendSegment("addresses").appendIndexAndSegment(1, "key"));
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the to string
	 */
	public void testToString() {
		replay(serializer, propertyResolver);
		assertEquals("", context.appendSegment("").toString());
		assertEquals("listing", context.appendSegment("listing").toString());
		assertEquals("listing.name", context.appendSegment("listing").appendSegment("name").toString());
		assertEquals("listing.addresses{}", context.appendSegment("listing").appendSegment("addresses").appendMap().toString());
		assertEquals("listing.addresses{home}", context.appendSegment("listing").appendSegment("addresses").appendMap("home").toString());
		assertEquals("listing.orders[1]", context.appendSegment("listing").appendSegment("orders").appendIndex(1).toString());
		assertEquals("addresses[1].key", context.appendSegment("addresses").appendIndexAndSegment(1, "key").toString());
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the Hashcode method
	 */
	public void testHashCode() {
		replay(serializer, propertyResolver);
		assertEquals(("listing.addresses{home}" + serializer + beans + propertyResolver).hashCode(), context.appendSegment("listing").appendSegment("addresses").appendMap("home").hashCode());
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the equals when not a serialization context
	 */
	public void testEqualsAgainstANonSerializationContext() {
		replay(serializer, propertyResolver);
		assertFalse(context.equals(new Object()));
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the equals when has different path
	 */
	public void testEqualsWhenDifferentPath() {
		replay(serializer, propertyResolver);
		assertFalse(context.equals(context.appendSegment("listing")));
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the equals when has different serializer
	 */
	public void testEqualsWhenDifferentSerializer() {
		Serializer serializer2 = createMock(Serializer.class);
		SerializationContext context2 = new SerializationContext(serializer2, beans, propertyResolver);
		replay(serializer, serializer2, propertyResolver);
		assertFalse(context.equals(context2));
		verify(serializer, serializer2, propertyResolver);
	}

	/**
	 * Tests the equals when has different map of beans
	 */
	public void testEqualsWhenDifferentBeans() {
		SerializationContext context2 = new SerializationContext(serializer, new HashMap<String, Object>(), propertyResolver);
		replay(serializer, propertyResolver);
		assertFalse(context.equals(context2));
		verify(serializer, propertyResolver);
	}

	/**
	 * Tests the equals when has different property resolver
	 */
	public void testEqualsWhenDifferentResolver() {
		PropertyResolver propertyResolver2 = createMock(PropertyResolver.class);
		SerializationContext context2 = new SerializationContext(serializer, beans, propertyResolver2);
		replay(serializer, propertyResolver, propertyResolver2);
		assertFalse(context.equals(context2));
		verify(serializer, propertyResolver, propertyResolver2);
	}
}
