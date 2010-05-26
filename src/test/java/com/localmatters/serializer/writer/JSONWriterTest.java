package com.localmatters.serializer.writer;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.serialization.CommentSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.serializer.test.DummyObject;
import com.localmatters.serializer.writer.JSONWriter;
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>JSONWriter</code>
 */
public class JSONWriterTest extends TestCase {
	private JSONWriter writer;
	private SerializationContext ctx;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new JSONWriter();
		ctx = new SerializationContext(writer, new HashMap<String, Object>(), null, false);
	}
	
	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Serialization serialization = createMock(Serialization.class);
		Object root = new Object();
		expect(serialization.getName()).andReturn("listing");
		expect(serialization.serialize(root, ctx)).andReturn("{}");
		replay(serialization);
		String result = writer.writeRoot(serialization, root, ctx);
		verify(serialization);
		assertEquals("{\"listing\":{}}", result);
	}
	
	/**
	 * Tests serializing a comment
	 */
	public void testComment() {
		CommentSerialization serialization = createMock(CommentSerialization.class);
		replay(serialization);
		String result = writer.writeComment(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing a null value that should not be written
	 */
	public void testValueWhenNullAndNotWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeValue(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null value that should be written
	 */
	public void testValueWhenNullAndWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeValue(serialization, null, ctx);
		verify(serialization);
		assertEquals("null", result);
	}
	
	/**
	 * Tests serializing an empty value that should not be written
	 */
	public void testValueWhenEmptyAndNotWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeValue(serialization, StringUtils.EMPTY, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing an empty value that should be written
	 */
	public void testValueWhenEmptyAndWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeValue(serialization, StringUtils.EMPTY, ctx);
		verify(serialization);
		assertEquals("\"\"", result);
	}
	
	/**
	 * Tests serializing a number
	 */
	public void testValueWhenNumber() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		replay(serialization);
		String result = writer.writeValue(serialization, 123456, ctx);
		verify(serialization);
		assertEquals("123456", result);
	}
	
	/**
	 * Tests serializing a value
	 */
	public void testValue() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		replay(serialization);
		String result = writer.writeValue(serialization, "hotel & café", ctx);
		verify(serialization);
		assertEquals("\"hotel &amp; caf&#233;\"", result);
	}

	/**
	 * Tests serializing a null object that should not be written
	 */
	public void testComplexWhenNullAndNotWrite() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing a null object that should be written
	 */
	public void testComplexWhenNullAndWrite() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, null, ctx);
		verify(serialization);
		assertEquals("null", result);
	}
	
	/**
	 * Tests serializing a object without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, new Object(), ctx);
		verify(serialization);
		assertEquals("{}", result);
	}
	
	/**
	 * Tests serializing a object for which all its properties are null
	 */
	public void testComplexWithOnlyNullProperties() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("{}", result);
	}
	
	/**
	 * Tests serializing a object
	 */
	public void testObject() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		listing.setId("123456");
		listing.setName("LocalMatters");
		
		expect(attributeSerialization.getName()).andReturn("id");
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("\"123456\"");
		expect(elementSerialization.getName()).andReturn("name");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("\"LocalMatters\"");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("{\"id\":\"123456\",\"name\":\"LocalMatters\"}", result);
	}
	
	/**
	 * Tests serializing an attribute
	 */
	public void testAttribute() {
		Serialization serialization = createMock(Serialization.class);
		ValueSerialization delegateSerialization = createMock(ValueSerialization.class);
		replay(serialization, delegateSerialization);
		String result = writer.writeAttribute(serialization, "hotel & café", ctx);
		verify(serialization, delegateSerialization);
		assertEquals("\"hotel &amp; caf&#233;\"", result);
	}

	/**
	 * Tests serializing an empty list that should not be written
	 */
	public void testListWhenEmptyAndNotWrite() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeIterator(serialization, null, Collections.EMPTY_LIST.iterator(), ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing an empty list that should  be written
	 */
	public void testListWhenEmptyAndWrite() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeIterator(serialization, null, Collections.EMPTY_LIST.iterator(), ctx);
		verify(serialization);
		assertEquals("[]", result);
	}

	/**
	 * Tests serializing a list that contains only null elements
	 */
	public void testListWhenContainsOnlyNullElements() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		Iterator<String> elements = CollectionUtils.asList("").iterator();
		
		expect(elementSerialization.serialize("", ctx.appendSegment("sports[0]"))).andReturn("");
		
		replay(serialization, elementSerialization);
		String result = writer.writeIterator(serialization, elementSerialization, elements, ctx.appendSegment("sports"));
		verify(serialization, elementSerialization);
		assertEquals("[]", result);
	}

	/**
	 * Tests serializing a list
	 */
	public void testList() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		Iterator<String> elements = CollectionUtils.asList("baseball", "hockey").iterator();
		
		expect(elementSerialization.serialize("baseball", ctx.appendSegment("sports[0]"))).andReturn("\"baseball\"");
		expect(elementSerialization.serialize("hockey", ctx.appendSegment("sports[1]"))).andReturn("\"hockey\"");
		
		replay(serialization, elementSerialization);
		String result = writer.writeIterator(serialization, elementSerialization, elements, ctx.appendSegment("sports"));
		verify(serialization, elementSerialization);
		assertEquals("[\"baseball\",\"hockey\"]", result);
	}


	/**
	 * Tests serializing an empty map that should not be written
	 */
	public void testMapWhenEmptyAndNotWrite() throws Exception {
		Serialization serialization = createMock(MapSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeMap(serialization, null, null, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing an empty map that should be written
	 */
	public void testMapWhenEmptyAndWrite() throws Exception {
		Serialization serialization = createMock(MapSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeMap(serialization, null, null, null, ctx);
		verify(serialization);
		assertEquals("{}", result);
	}

	/**
	 * Tests serializing a map
	 */
	public void testMap() throws Exception {
		Serialization serialization = createMock(MapSerialization.class);
		Serialization keySerialization = createMock(Serialization.class);
		Serialization valueSerialization = createMock(Serialization.class);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		expect(keySerialization.serialize("sport", ctx.appendSegment("leisures{}"))).andReturn("\"sport\"");
		expect(valueSerialization.serialize("baskeball", ctx.appendSegment("leisures{\"sport\"}"))).andReturn("\"baskeball\"");
		expect(keySerialization.serialize("hobby", ctx.appendSegment("leisures{}"))).andReturn("\"hobby\"");
		expect(valueSerialization.serialize("photography", ctx.appendSegment("leisures{\"hobby\"}"))).andReturn("\"photography\"");
		
		replay(serialization, keySerialization, valueSerialization);
		String result = writer.writeMap(serialization, keySerialization, valueSerialization, map, ctx.appendSegment("leisures"));
		verify(serialization, keySerialization, valueSerialization);
		assertEquals("{\"sport\":\"baskeball\",\"hobby\":\"photography\"}", result);
	}
}
