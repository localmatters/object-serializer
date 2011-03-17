/*
   Copyright 2010-present Local Matters, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.localmatters.serializer.writer;

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

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.resolver.InvalidPropertyException;
import org.localmatters.serializer.resolver.PropertyResolver;
import org.localmatters.serializer.serialization.IOSerializationException;
import org.localmatters.serializer.serialization.NameExpectedException;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.UnknownPropertyException;


/**
 * Test the <code>AbstractWriter</code>
 */
public class AbstractWriterTest extends TestCase {
	private AbstractWriter writer;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected void setUp() throws Exception {
		writer = new AbstractWriter() {
			public void writeValue(Serialization ser, String name, Object value, SerializationContext ctx) {}
			public void writeRoot(Serialization ser, Object root, SerializationContext ctx) {}
			public void writeMap(Serialization ser, String name, Collection<Map.Entry> entries, String key, Serialization value, Collection<String> comments, SerializationContext ctx) {}
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
	
	/**
	 * Tests checking the required name when it is missing
	 */
	public void testCheckRequiredNameWhenMissing() {
		SerializationContext ctx = new SerializationContext(writer, null, null);
		try {
			AbstractWriter.checkRequiredName(ctx, null);
			fail("NameExpectedException expected");
		} catch (NameExpectedException e) {
		}
	}
	
	/**
	 * Tests checking the required name
	 */
	public void testCheckRequiredName() throws Exception {
		SerializationContext ctx = new SerializationContext(writer, null, null);
		assertEquals("listing", AbstractWriter.checkRequiredName(ctx, "listing"));
	}
}
