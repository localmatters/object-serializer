package org.localmatters.serializer.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.localmatters.serializer.util.SerializationUtils.createConstantAttribute;
import static org.localmatters.serializer.util.SerializationUtils.createConstantValue;
import static org.localmatters.serializer.util.SerializationUtils.createName;
import static org.localmatters.serializer.util.SerializationUtils.createValue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.resolver.PropertyResolver;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.IteratorSerialization;
import org.localmatters.serializer.serialization.MapSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;
import org.localmatters.serializer.writer.JSONWriter;


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
		assertEquals("\n      ", writer.getPrefix(ctx));
		assertEquals("results.listings.address", ctx.getPath());
	}

	/**
	 * Tests the root serialization with formatting
	 */
	public void testRootWithFormatting() throws Exception {
		ctx.setFormatting(true);
		Serialization ser = createValue("listing");
		String root = "12345 Hotel";
		writer.writeRoot(ser, root, ctx);
		assertEquals("{\n\"listing\": \"12345 Hotel\"\n}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests the root serialization when named complex
	 */
	public void testRootWhenNamedComplex() throws Exception {
		ComplexSerialization complex = new ComplexSerialization();
		complex.addElement(createConstantAttribute("id", "123456"));
		complex.addElement(createConstantAttribute("name", "12345 \"Hotel\" & spa"));
		Serialization ser = createName("listing", complex);
		writer.writeRoot(ser, null, ctx);
		assertEquals("{\"id\": \"123456\", \"name\": \"12345 \\\"Hotel\\\" & spa\"}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests the root serialization when complex
	 */
	public void testRootWhenList() throws Exception {
		IteratorSerialization ser = new IteratorSerialization();
		ser.setElement(new ValueSerialization());
		writer.writeRoot(ser, Arrays.asList("hello", "world"), ctx);
		assertEquals("[\"hello\", \"world\"]", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Serialization ser = createValue("listing");
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
		ctx.nextLevel("something").setFormatting(true);
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeValue(ser, null, null, ctx);
		verify(ser);
		assertEquals("\n   null", os.toString());
		assertEquals("something", ctx.getPath());
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
		assertEquals("\n   \"id\": 123456", os.toString());
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
		assertEquals("\"hotel & café\"", os.toString());
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
		assertEquals("\n   \"name\": \"hotel & café\"", os.toString());
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
		writer.writeComplex(ser, "listing", null, null, null, Arrays.asList("just a listing"), ctx);
		verify(ser);
		assertEquals("\n   \"listing\": {}", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a complex without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Serialization ser = createMock(ComplexSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeComplex(ser, "listing", new Object(), null, null, null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a complex with formatting
	 */
	public void testComplexWhenFormatting() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization element1 = createConstantValue("desc", null);
		Serialization element2 = createConstantAttribute("id", "ABCD1234");
		Serialization element3 = createConstantAttribute("address", null);
		Serialization element4 = createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, null, Arrays.asList(element1, element2, element3, element4), null, ctx);
		verify(ser);

		assertEquals("\n   \"listing\": {\n      \"id\": \"ABCD1234\", \n      \"name\": \"John Hotel\"\n   }", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a complex that has no name
	 */
	public void testComplexWithNoName() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute1 = createConstantAttribute("desc", null);
		Serialization attribute2 = createConstantAttribute("id", "ABCD1234");
		Serialization element = createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, null, object, Arrays.asList(attribute1, attribute2), Arrays.asList(element), null, ctx);
		verify(ser);

		assertEquals("{\"id\": \"ABCD1234\", \"name\": \"John Hotel\"}", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a complex without elements
	 */
	public void testComplexWithoutElement() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute1 = createConstantAttribute("desc", null);
		Serialization attribute2 = createConstantAttribute("id", "ABCD1234");
		Serialization attribute3 = createConstantAttribute("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, Arrays.asList(attribute1, attribute2, attribute3), null, null, ctx);
		verify(ser);

		assertEquals("\"listing\": {\"id\": \"ABCD1234\", \"name\": \"John Hotel\"}", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a complex
	 */
	public void testComplex() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute1 = createConstantAttribute("desc", null);
		Serialization attribute2 = createConstantAttribute("id", "ABCD1234");
		Serialization element = createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, Arrays.asList(attribute1, attribute2), Arrays.asList(element), null, ctx);
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
	public void testIteratorWithNoName() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(IteratorSerialization.class);
		Iterator<String> itr = Arrays.asList("baseball", "hockey", null).iterator();
		Serialization element = new ValueSerialization();
		
		replay(ser);
		writer.writeIterator(ser, null, itr, "sport", element, null, ctx);
		verify(ser);
		assertEquals("\n   [\n      \"baseball\", \n      \"hockey\"\n   ]", os.toString());
		assertEquals("results", ctx.getPath());
	}

	/**
	 * Tests serializing a iterator
	 */
	public void testIterator() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(IteratorSerialization.class);
		Iterator<String> itr = Arrays.asList("baseball", "hockey", null).iterator();
		Serialization element = new ValueSerialization();
		
		replay(ser);
		writer.writeIterator(ser, "sports", itr, "sport", element, null, ctx);
		verify(ser);
		assertEquals("\n   \"sports\": [\n      \"baseball\", \n      \"hockey\"\n   ]", os.toString());
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
		assertEquals("\n   \"addresses\": {}", os.toString());
		assertEquals("results", ctx.getPath());
	}

	/**
	 * Tests serializing a map when formatting
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testMapWhenFormatting() throws Exception {
		ctx.setFormatting(true);
		Serialization ser = createMock(MapSerialization.class);
		Serialization value = new ValueSerialization();
		Map map = new LinkedHashMap<String, String>();
		map.put("empty", null);
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		replay(ser);
		writer.writeMap(ser, "leisures", map.entrySet(), null, value, null, ctx);
		verify(ser);
		assertEquals("\n\"leisures\": {\n   \"sport\": \"baskeball\", \n   \"hobby\": \"photography\"\n}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testMapWithNoName() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		ctx = new SerializationContext(writer, resolver, os);
		
		Serialization ser = createMock(MapSerialization.class);
		Serialization value = new ValueSerialization();
		Map map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		expect(resolver.resolve("sport", "keyProperty")).andReturn("s");
		expect(resolver.resolve("hobby", "keyProperty")).andReturn("h");
		
		replay(ser, resolver);
		writer.writeMap(ser, null, map.entrySet(), "keyProperty", value, null, ctx);
		verify(ser, resolver);
		assertEquals("{\"s\": \"baskeball\", \"h\": \"photography\"}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testMap() throws Exception {
		PropertyResolver resolver = createMock(PropertyResolver.class);
		ctx = new SerializationContext(writer, resolver, os);
		
		Serialization ser = createMock(MapSerialization.class);
		Serialization value = new ValueSerialization();
		Map map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		expect(resolver.resolve("sport", "keyProperty")).andReturn("s");
		expect(resolver.resolve("hobby", "keyProperty")).andReturn("h");
		
		replay(ser, resolver);
		writer.writeMap(ser, "leisures", map.entrySet(), "keyProperty", value, null, ctx);
		verify(ser, resolver);
		assertEquals("\"leisures\": {\"s\": \"baskeball\", \"h\": \"photography\"}", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
}
