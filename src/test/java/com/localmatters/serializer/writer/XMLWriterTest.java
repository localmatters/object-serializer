package com.localmatters.serializer.writer;

import static com.localmatters.serializer.util.SerializationUtils.createConstantAttribute;
import static com.localmatters.serializer.util.SerializationUtils.createConstantValue;
import static com.localmatters.serializer.util.SerializationUtils.createValue;
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

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;

/**
 * Tests the <code>XMLWriter</code>
 */
public class XMLWriterTest extends TestCase {
	private XMLWriter writer;
	private SerializationContext ctx;
	private OutputStream os;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new XMLWriter();
		os = new ByteArrayOutputStream();
		ctx = new SerializationContext(writer, null, os);
	}
	
	/**
	 * Tests getting the prefix when the writer is not formatting
	 */
	public void testGettingPrefixWhenNotFormatting() {
		ctx.nextLevel("results").nextLevel("listings").nextLevel("address");
		assertEquals(StringUtils.EMPTY, writer.getPrefix(ctx));
	}
	
	/**
	 * Tests getting the prefix when the writer is formatting
	 */
	public void testGettingPrefix() {
		ctx.setFormatting(true);
		ctx.nextLevel("results").nextLevel("listings").nextLevel("address");
		assertEquals("\n        ", writer.getPrefix(ctx));
	}
	
	/**
	 * Tests writing comments when not formatting
	 */
	public void testWriteCommentsWhenNotFormating() throws Exception {
		writer.writeComments(ctx, "\n    ", CollectionUtils.asList("Hello -- World", "What's up?"));
		assertEquals(StringUtils.EMPTY, os.toString());
	}
	
	
	/**
	 * Tests writing comments when formatting but not comment
	 */
	public void testWriteCommentsWhenEmpty() throws Exception {
		ctx.setFormatting(true);
		writer.writeComments(ctx, "\n    ", null);
		assertEquals("\n    ", os.toString());
	}
	
	/**
	 * Tests writing the prefix with comment when the context is not pretty
	 */
	public void testWritePrefixWithComment() throws Exception {
		ctx.setFormatting(true);
		writer.writeComments(ctx, "\n    ", CollectionUtils.asList("Hello -- World", "What's up?"));
		assertEquals("\n\n    <!-- Hello ** World\n         What's up? -->\n    ", os.toString());
	}

	/**
	 * Tests the root serialization with formatting
	 */
	public void testRootWithFormatting() throws Exception {
		ctx.setFormatting(true);
		Serialization ser = createValue("listing");
		String root = "12345 Hotel";
		writer.writeRoot(ser, root, ctx);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<listing>12345 Hotel</listing>", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Serialization ser = createValue("listing");
		String root = "12345 Hotel";
		writer.writeRoot(ser, root, ctx);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><listing>12345 Hotel</listing>", os.toString());
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
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeValue(ser, null, null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null value that should be written, but its name is
	 * null
	 */
	public void testValueWhenNullAndWrite() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeValue(ser, "name", null, ctx);
		verify(ser);
		assertEquals("<name/>", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a non-null or empty value but with an empty name
	 */
	public void testValueWhenNameBlank() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, null, "John Doé", ctx);
		verify(ser);
		assertEquals("John Do&#233;", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing value with prefix
	 */
	public void testValueWithPrefix() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("listing");
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, "name", "John Doé", ctx);
		verify(ser);
		assertEquals("\n    <name>John Do&#233;</name>", os.toString());
		assertEquals(1, ctx.getDeepness());
		assertEquals("listing", ctx.getPath());
	}
	
	/**
	 * Tests serializing a non-null or empty value
	 */
	public void testValue() throws Exception {
		ValueSerialization ser = createMock(ValueSerialization.class);
		replay(ser);
		writer.writeValue(ser, "name", "John Doé", ctx);
		verify(ser);
		assertEquals("<name>John Do&#233;</name>", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}

	/**
	 * Tests serializing a null attribute that should not be written
	 */
	public void testAttributeWhenNullAndNotWrite() throws Exception {
		Serialization ser = createMock(Serialization.class);
		expect(ser.isWriteEmpty()).andReturn(false);
		replay(ser);
		writer.writeAttribute(ser, "name", null, ctx);
		verify(ser);
		assertEquals(StringUtils.EMPTY, os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null attribute that should be written
	 */
	public void testAttributeWhenNullAndWrite() throws Exception {
		Serialization ser = createMock(Serialization.class);
		expect(ser.isWriteEmpty()).andReturn(true);
		replay(ser);
		writer.writeAttribute(ser, "name", null, ctx);
		verify(ser);
		assertEquals(" name=\"\"", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing an attribute
	 */
	public void testAttribute() throws Exception {
		Serialization ser = createMock(Serialization.class);
		replay(ser);
		writer.writeAttribute(ser, "name", "John Doé", ctx);
		verify(ser);
		assertEquals(" name=\"John Do&#233;\"", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a null complex that should not be written
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
		assertEquals("\n\n    <!-- just a listing -->\n    <listing/>", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a object without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Serialization ser = createMock(ComplexSerialization.class);
		replay(ser);
		writer.writeComplex(ser, "listing", new Object(), null, null, null, ctx);
		verify(ser);
		assertEquals("<listing/>", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
	
	/**
	 * Tests serializing a object that has only sub-element
	 */
	public void testComplexWithOnlySubElements() throws Exception {
		ctx.setFormatting(true);
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization element1 = createConstantValue("id", "ABCD1234");
		Serialization element2 = createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, null, CollectionUtils.asList(element1, element2), null, ctx);
		verify(ser);

		assertEquals("\n    <listing>\n        <id>ABCD1234</id>\n        <name>John Hotel</name>\n    </listing>", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a object that has only attributes
	 */
	public void testComplexWithOnlyAttributes() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute1 = createConstantAttribute("id", "ABCD1234");
		Serialization attribute2 = createConstantAttribute("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, CollectionUtils.asList(attribute1, attribute2), null, CollectionUtils.asList("just a listing"), ctx);
		verify(ser);

		assertEquals("<listing id=\"ABCD1234\" name=\"John Hotel\"/>", os.toString());
		assertEquals("results", ctx.getPath());
	}
	
	/**
	 * Tests serializing a object
	 */
	public void testComplex() throws Exception {
		ctx.nextLevel("results");
		Serialization ser = createMock(ComplexSerialization.class);
		Serialization attribute = createConstantAttribute("id", "ABCD1234");
		Serialization element = createConstantValue("name", "John Hotel");
		Object object = new Object();
		
		replay(ser);
		writer.writeComplex(ser, "listing", object, CollectionUtils.asList(attribute), CollectionUtils.asList(element), CollectionUtils.asList("just a listing"), ctx);
		verify(ser);

		assertEquals("<listing id=\"ABCD1234\"><name>John Hotel</name></listing>", os.toString());
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
		assertEquals("<orders/>", os.toString());
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
		assertEquals("\n    <sports>\n        <sport>baseball</sport>\n        <sport>hockey</sport>\n    </sports>", os.toString());
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
		assertEquals("\n    <addresses/>", os.toString());
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
		assertEquals("\n<leisures>\n    <sport>baskeball</sport>\n    <hobby>photography</hobby>\n</leisures>", os.toString());
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
		assertEquals("<leisures><s>baskeball</s><h>photography</h></leisures>", os.toString());
		assertEquals(StringUtils.EMPTY, ctx.getPath());
	}
}
