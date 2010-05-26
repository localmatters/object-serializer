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
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>XMLWriter</code>
 */
public class XMLWriterTest extends TestCase {
	private XMLWriter writer;
	private SerializationContext ctx;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new XMLWriter();
		ctx = new SerializationContext(writer, new HashMap<String, Object>(), null, false);
	}
	
	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Serialization serialization = createMock(Serialization.class);
		Object root = new Object();
		expect(serialization.serialize(root, ctx)).andReturn("<listing/>");
		replay(serialization);
		String result = writer.writeRoot(serialization, root, ctx);
		verify(serialization);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><listing/>", result);
	}
	
	/**
	 * Tests serializing a null comment that should not be written
	 */
	public void testCommentWhenNullAndNotWrite() {
		CommentSerialization serialization = createMock(CommentSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeComment(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null comment that should be written
	 */
	public void testCommentWhenNullAndWrite() {
		CommentSerialization serialization = createMock(CommentSerialization.class);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeComment(serialization, null, ctx);
		verify(serialization);
		assertEquals("\n<!--  -->", result);
	}
	
	/**
	 * Tests serializing a comment
	 */
	public void testComment() {
		CommentSerialization serialization = createMock(CommentSerialization.class);
		replay(serialization);
		String result = writer.writeComment(serialization, "Hello -- World", ctx);
		verify(serialization);
		assertEquals("\n<!-- Hello ** World -->", result);
	}
	
	/**
	 * Tests serializing a null value that should not be written
	 */
	public void testValueWhenNullAndNotWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.getName()).andReturn("name");
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeValue(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null value that should be written, but its name is
	 * null
	 */
	public void testValueWhenNullAndWriteButNameNull() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.getName()).andReturn(null);
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeValue(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing an empty value
	 */
	public void testValueWhenEmptyAndWrite() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.getName()).andReturn("name");
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeValue(serialization, "  ", ctx);
		verify(serialization);
		assertEquals("<name/>", result);
	}
	
	/**
	 * Tests serializing a non-null or empty value but with an empty name
	 */
	public void testValueWhenNameBlank() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.getName()).andReturn(null);
		replay(serialization);
		String result = writer.writeValue(serialization, "John Doé", ctx);
		verify(serialization);
		assertEquals("John Do&#233;", result);
	}
	
	/**
	 * Tests serializing a non-null or empty value
	 */
	public void testValue() {
		ValueSerialization serialization = createMock(ValueSerialization.class);
		expect(serialization.getName()).andReturn("name");
		replay(serialization);
		String result = writer.writeValue(serialization, "John Doé", ctx);
		verify(serialization);
		assertEquals("<name>John Do&#233;</name>", result);
	}
	
	/**
	 * Tests serializing a null complex that should not be written
	 */
	public void testComplexWhenNullAndNotWrite() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		expect(serialization.getName()).andReturn("listing");
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, null, ctx.appendSegment("listing"));
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null object that should be written
	 */
	public void testComplexWhenNullAndWrite() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		expect(serialization.getName()).andReturn("listing");
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, null, ctx.appendSegment("listing"));
		verify(serialization);
		assertEquals("<listing/>", result);
	}
	
	/**
	 * Tests serializing a object without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		expect(serialization.getName()).andReturn("listing");
		replay(serialization);
		String result = writer.writeComplex(serialization, null, null, new Object(), ctx.appendSegment("listing"));
		verify(serialization);
		assertEquals("<listing/>", result);
	}
	
	/**
	 * Tests serializing a object for which all its properties are null
	 */
	public void testComplexWithOnlyNullProperties() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		
		expect(serialization.getName()).andReturn("listing");
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("<listing/>", result);
	}
	
	/**
	 * Tests serializing a object for which all non-null properties are 
	 * sub-elements
	 */
	public void testComplexWithOnlySubElements() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		listing.setName("LocalMatters");
		
		expect(serialization.getName()).andReturn("listing");
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("<name>LocalMatters</name>");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("<listing><name>LocalMatters</name></listing>", result);
	}
	
	/**
	 * Tests serializing a object for which all non-null properties are attributes
	 */
	public void testComplexWithOnlyAttributes() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		listing.setId("123456");
		
		expect(serialization.getName()).andReturn("listing");
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn(" id=\"123456\"");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("<listing id=\"123456\"/>", result);
	}
	
	/**
	 * Tests serializing a object with attributes and sub-elements
	 */
	public void testComplex() throws Exception {
		Serialization serialization = createMock(ComplexSerialization.class);
		Serialization attributeSerialization = createMock(Serialization.class);
		Serialization elementSerialization = createMock(Serialization.class);
		DummyObject listing = new DummyObject();
		listing.setId("123456");
		listing.setName("LocalMatters");
		
		expect(serialization.getName()).andReturn("listing");
		expect(attributeSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn(" id=\"123456\"");
		expect(elementSerialization.serialize(listing, ctx.appendSegment("listing"))).andReturn("<name>LocalMatters</name>");
		
		replay(serialization, attributeSerialization, elementSerialization);
		String result = writer.writeComplex(serialization, 
				CollectionUtils.asList(attributeSerialization), 
				CollectionUtils.asList(elementSerialization), 
				listing, ctx.appendSegment("listing"));
		verify(serialization, attributeSerialization, elementSerialization);
		assertEquals("<listing id=\"123456\"><name>LocalMatters</name></listing>", result);
	}
	
	/**
	 * Tests serializing a null attribute that should not be written
	 */
	public void testAttributeWhenNullAndNotWrite() {
		Serialization serialization = createMock(Serialization.class);
		expect(serialization.isWriteEmpty()).andReturn(false);
		replay(serialization);
		String result = writer.writeAttribute(serialization, null, ctx);
		verify(serialization);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null attribute that should be written
	 */
	public void testAttributeWhenNullAndWrite() {
		Serialization serialization = createMock(Serialization.class);
		expect(serialization.getName()).andReturn("name");
		expect(serialization.isWriteEmpty()).andReturn(true);
		replay(serialization);
		String result = writer.writeAttribute(serialization, null, ctx);
		verify(serialization);
		assertEquals(" name=\"\"", result);
	}
	
	/**
	 * Tests serializing an attribute
	 */
	public void testAttribute() {
		Serialization serialization = createMock(Serialization.class);
		expect(serialization.getName()).andReturn("name");
		replay(serialization);
		String result = writer.writeAttribute(serialization, "John Doé", ctx);
		verify(serialization);
		assertEquals(" name=\"John Do&#233;\"", result);
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
	 * Tests serializing a list that contains only null elements
	 */
	public void testListWhenContainsOnlyNullElements() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		Iterator<String> elements = CollectionUtils.asList("").iterator();
		Serialization elementSerialization = createMock(Serialization.class);
		
		expect(elementSerialization.serialize("", ctx.appendSegment("sports[0]"))).andReturn("");
		expect(serialization.isWriteEmpty()).andReturn(true);
		expect(serialization.getName()).andReturn("sports");
		
		replay(serialization, elementSerialization);
		String result = writer.writeIterator(serialization, elementSerialization, elements, ctx.appendSegment("sports"));
		verify(serialization, elementSerialization);
		assertEquals("<sports/>", result);
	}

	/**
	 * Tests serializing a list
	 */
	public void testList() throws Exception {
		Serialization serialization = createMock(IteratorSerialization.class);
		Iterator<String> elements = CollectionUtils.asList("baseball", "hockey").iterator();
		Serialization elementSerialization = createMock(Serialization.class);
		
		expect(elementSerialization.serialize("baseball", ctx.appendSegment("sports[0]"))).andReturn("<sport>baseball</sport>");
		expect(elementSerialization.serialize("hockey", ctx.appendSegment("sports[1]"))).andReturn("<sport>hockey</sport>");
		expect(serialization.getName()).andReturn("sports");
		
		replay(serialization, elementSerialization);
		String result = writer.writeIterator(serialization, elementSerialization, elements, ctx.appendSegment("sports"));
		verify(serialization, elementSerialization);
		assertEquals("<sports><sport>baseball</sport><sport>hockey</sport></sports>", result);
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
		expect(serialization.getName()).andReturn("leisure");
		replay(serialization);
		String result = writer.writeMap(serialization, null, null, null, ctx);
		verify(serialization);
		assertEquals("<leisure/>", result);
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
		
		expect(keySerialization.serialize("sport", ctx.appendSegment("leisures{}"))).andReturn("sport");
		expect(valueSerialization.serialize("baskeball", ctx.appendSegment("leisures{sport}"))).andReturn("baskeball");
		expect(keySerialization.serialize("hobby", ctx.appendSegment("leisures{}"))).andReturn("hobby");
		expect(valueSerialization.serialize("photography", ctx.appendSegment("leisures{hobby}"))).andReturn("photography");
		expect(serialization.getName()).andReturn("leisure");
		
		replay(serialization, keySerialization, valueSerialization);
		String result = writer.writeMap(serialization, keySerialization, valueSerialization, map, ctx.appendSegment("leisures"));
		verify(serialization, keySerialization, valueSerialization);
		assertEquals("<leisure><sport>baskeball</sport><hobby>photography</hobby></leisure>", result);
	}
}
