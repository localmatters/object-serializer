package com.localmatters.serializer.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.serializer.util.SerializationUtils;
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>JSONWriter</code>
 */
public class JSONWriterTest extends TestCase {
	private JSONWriter writer;
	private SerializationContext ctx;
	private OutputStream os;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new JSONWriter();
		os = new ByteArrayOutputStream();
		ctx = new SerializationContext(writer, null, os);
	}
	
	/**
	 * Tests getting the prefix when the writer is not formatting
	 */
	public void testGettingPrefixWhenNotFormatting() {
		ctx.nextLevel("results").nextLevel("listings").nextLevel("address");
		assertEquals(StringUtils.EMPTY, writer.getPrefix(ctx));
		assertEquals("results.listings.address", ctx.getPath());
	}
	
	/**
	 * Tests getting the prefix when the writer is formatting
	 */
	public void testGettingPrefix() {
		ctx.setFormatting(true);
		ctx.nextLevel("results").nextLevel("listings").nextLevel("address");
		assertEquals("\n         ", writer.getPrefix(ctx));
		assertEquals("results.listings.address", ctx.getPath());
	}

	/**
	 * Tests the root serialization with formatting
	 */
	public void testRootWithFormatting() throws Exception {
		ctx.setFormatting(true);
		Serialization ser = SerializationUtils.createValue("listing");
		String root = "12345 Hotel";
		writer.writeRoot(ser, root, ctx);
		assertEquals("{\n   \"listing\": \"12345 Hotel\"\n}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Serialization ser = SerializationUtils.createValue("listing");
		String root = "12345 Hotel";
		writer.writeRoot(ser, root, ctx);
		assertEquals("{\"listing\": \"12345 Hotel\"}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a null value that should not be written
	 */
	public void testValueWhenNullAndNotWrite() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeValue(ser, "name", null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null value that should be written, but its name is
	 * null
	 */
	public void testValueWhenNullAndWriteButNameNull() throws Exception {
		ctx.setFormatting(true);
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeValue(ser, null, null, ctx);
		verify(ser);
		assertEquals("\n   null", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null value that should be written
	 */
	public void testValueWhenNullAndWrite() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeValue(ser, "name", null, ctx);
		verify(ser);
		assertEquals("\"name\": null", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a number
	 */
	public void testValueWhenNumber() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("listing");
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, "id", 123456, ctx);
		verify(ser);
		assertEquals("\n      \"id\": 123456", os.toString());
		assertEquals("listing", ctx.getPath());
	}
	
	/**
	 * Tests serializing a boolean
	 */
	public void testValueWhenBoolean() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, "sold", true, ctx);
		verify(ser);
		assertEquals("\"sold\": true", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a value
	 */
	public void testValueAndNameNull() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, null, "hotel & café", ctx);
		verify(ser);
		assertEquals("\"hotel &amp; caf&#233;\"", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing an attribute
	 */
	public void testAttribute() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("listing");
		Serialization ser = createMock(Serialization.class);
		ValueSerialization delegate = createMock(ValueSerialization.class);
		replay(ser, delegate);
		writer.writeAttribute(ser, "name", "hotel & café", ctx);
		verify(ser, delegate);
		assertEquals("\n      \"name\": \"hotel &amp; caf&#233;\"", os.toString());
		assertEquals("listing", ctx.getPath());
	}

	/**
	 * Tests serializing a null object that should not be written
	 */
	public void testComplexWhenNullAndNotWrite() throws Exception {
		Serialization ser = createMock(ComplexSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeComplex(ser, "listing", null, null, null, null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null object that should be written
	 */
	public void testComplexWhenNullAndWrite() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeComplex(ser, "listing", null, null, null, CollectionUtils.asList("just a listing"), ctx);
		verify(ser);
		assertEquals("\n      \"listing\": {}", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing an object without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Serialization ser = createMock(ComplexSerialization.class);
		replay(ser);
		writer.writeComplex(ser, "listing", new Object(), null, null, null, ctx);
		verify(ser);
		assertEquals("\"listing\": {}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a object with formatting
	 */
	public void testComplexWhenFormatting() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute = SerializationUtils.createConstantAttribute("id", "ABCD1234");
		Serialization element = SerializationUtils.createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, CollectionUtils.asList(attribute), CollectionUtils.asList(element), null, ctx);
		verify(ser);

		assertEquals("\n      \"listing\": {\n         \"id\": \"ABCD1234\", \n         \"name\": \"John Hotel\"\n      }", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a object that has only sub-element
	 */
	public void testComplex() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute = SerializationUtils.createConstantAttribute("id", "ABCD1234");
		Serialization element = SerializationUtils.createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, CollectionUtils.asList(attribute), CollectionUtils.asList(element), null, ctx);
		verify(ser);

		assertEquals("\"listing\": {\"id\": \"ABCD1234\", \"name\": \"John Hotel\"}", os.toString());
		assertEquals("results", ctx.getPath());
	}

	/**
	 * Tests serializing an empty iterator that should not be written
	 */
	public void testIteratorWhenEmptyAndNotWrite() throws Exception {
		ctx.nextLevel("listing");
		Serialization ser = createMock(IteratorSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeIterator(ser, "orders", Collections.EMPTY_LIST.iterator(), "order", null, null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals("listing", ctx.getPath());
	}

	/**
	 * Tests serializing an empty iterator that should be written
	 */
	public void testIteratorWhenEmptyAndWrite() throws Exception {
		Serialization ser = createMock(IteratorSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeIterator(ser,"orders", Collections.EMPTY_LIST.iterator(), "order", null, null, ctx);
		verify(ser);
		assertEquals("\"orders\": []", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a iterator
	 */
	public void testIterator() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(IteratorSerialization.class);
		Iterator<String> itr = CollectionUtils.asList("baseball", "hockey").iterator();
		Serialization element = new ValueSerialization();
		
		replay(ser);
		writer.writeIterator(ser, "sports", itr, "sport", element, null, ctx);
		verify(ser);
		assertEquals("\n      \"sports\": [\n         \"baseball\", \n         \"hockey\"\n      ]", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing an empty map that should not be written
	 */
	public void testMapWhenEmptyAndNotWrite() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(MapSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeMap(ser, "addresses", null, null, null, null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals("results", ctx.getPath());
	}

	/**
	 * Tests serializing an empty map that should be written
	 */
	public void testMapWhenEmptyAndWrite() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(MapSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeMap(ser, "addresses", null, null, null, null, ctx);
		verify(ser);
		assertEquals("\n      \"addresses\": {}", os.toString());
		assertEquals("results", ctx.getPath());
	}

	/**
	 * Tests serializing a map when formatting
	 */
	public void testMapWhenFormatting() throws Exception {
		ctx.setFormatting(true);
		Serialization ser = createMock(MapSerialization.class);
		Serialization value = new ValueSerialization();
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		replay(ser);
		writer.writeMap(ser, "leisures", map, null, value, null, ctx);
		verify(ser);
		assertEquals("\n   \"leisures\": {\n      \"sport\": \"baskeball\", \n      \"hobby\": \"photography\"\n   }", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a map
	 */
	public void testMap() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		ctx = new SerializationContext(writer, resolver, os);
		
		Serialization ser = createMock(MapSerialization.class);
		Serialization value = new ValueSerialization();
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		expect(resolver.resolve("sport", "keyProperty")).andReturn("s");
		expect(resolver.resolve("hobby", "keyProperty")).andReturn("h");
		
		replay(ser, resolver);
		writer.writeMap(ser, "leisures", map, "keyProperty", value, null, ctx);
		verify(ser, resolver);
		assertEquals("\"leisures\": {\"s\": \"baskeball\", \"h\": \"photography\"}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
}
