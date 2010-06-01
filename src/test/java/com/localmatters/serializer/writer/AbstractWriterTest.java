/**
 * 
 */
package com.localmatters.serializer.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.InvalidPropertyException;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.serialization.IOSerializationException;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.UnknownPropertyException;

/**
 * Test the <code>AbstractWriter</code>
 */
public class AbstractWriterTest extends TestCase {
	private AbstractWriter writer;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new AbstractWriter() {
			public void writeValue(Serialization ser, String name, Object value, SerializationContext ctx) {}
			public void writeRoot(Serialization ser, Object root, SerializationContext ctx) {}
			public void writeMap(Serialization ser, String name, Map<?, ?> map, String key, Serialization value, Collection<String> comments, SerializationContext ctx) {}
			public void writeIterator(Serialization ser, String name, Iterator<?> itr, String elementName, Serialization element, Collection<String> comments, SerializationContext ctx) {}
			public void writeComplex(Serialization ser, String name, Object complex, Collection<Serialization> attributes, Collection<Serialization> elements, Collection<String> comments, SerializationContext ctx) {}
			public void writeAttribute(Serialization ser, String name,Object attribute, SerializationContext ctx) {}
		};
	}

	/**
	 * Tests writing to the output stream when an IO error occurs
	 */
	public void testWriteWhenIoError() throws Exception {
		OutputStream os = createMock(OutputStream.class);
		SerializationContext ctx = new SerializationContext(writer, null, os);
		byte[] bytes = "string to write".getBytes();

		os.write(bytes);
		expectLastCall().andThrow(new IOException());

		replay(os);
		try {
			writer.write(ctx, bytes);
			fail("IOSerializationException expected");
		} catch (IOSerializationException e) {
		}
		verify(os);
	}

	/**
	 * Tests writing to the output stream 
	 */
	public void testWriteBytes() throws Exception {
		OutputStream os = new ByteArrayOutputStream();
		SerializationContext ctx = new SerializationContext(writer, null, os);
		writer.write(ctx, "string to write".getBytes());
		assertEquals("string to write", os.toString());
	}

	/**
	 * Tests writing to the output stream 
	 */
	public void testWriteString() throws Exception {
		OutputStream os = new ByteArrayOutputStream();
		SerializationContext ctx = new SerializationContext(writer, null, os);
		writer.write(ctx, "string to write");
		assertEquals("string to write", os.toString());
	}
	
	/**
	 * Tests resolving the map entry's key when the key property is not set
	 * @throws Exception 
	 */
	public void testResolveMapEntryKeyWhenNoProperty() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		SerializationContext ctx = new SerializationContext(writer, resolver, null);
		Map<String, String> map = new HashMap<String, String>();
		map.put("work", "1221 Auraria Pkwy");
		replay(resolver);
		assertEquals("work", AbstractWriter.resolvesMapKey(null, map.entrySet().iterator().next(), ctx));
		verify(resolver);
	}
	
	/**
	 * Tests resolving the map entry's key when the key property is set, but 
	 * invalid
	 * @throws Exception 
	 */
	public void testResolveMapEntryKeyWhenInvalidProperty() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		SerializationContext ctx = new SerializationContext(writer, resolver, null);
		Map<String, String> map = new HashMap<String, String>();
		map.put("work", "1221 Auraria Pkwy");
		expect(resolver.resolve("work", "first.letter")).andThrow(new InvalidPropertyException("first.letter", "String"));
		replay(resolver);
		try {
			AbstractWriter.resolvesMapKey("first.letter", map.entrySet().iterator().next(), ctx);
			fail("UnknownPropertyException epxected");
		} catch (UnknownPropertyException e) {
		}
		verify(resolver);
	}
	
	/**
	 * Tests resolving the map entry's key when the key property is set, but 
	 * invalid
	 * @throws Exception 
	 */
	public void testResolveMapEntryKey() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		SerializationContext ctx = new SerializationContext(writer, resolver, null);
		Map<String, String> map = new HashMap<String, String>();
		map.put("work", "1221 Auraria Pkwy");
		expect(resolver.resolve("work", "first.letter")).andReturn("w");
		replay(resolver);
		assertEquals("w", AbstractWriter.resolvesMapKey("first.letter", map.entrySet().iterator().next(), ctx));
		verify(resolver);
	}
}
