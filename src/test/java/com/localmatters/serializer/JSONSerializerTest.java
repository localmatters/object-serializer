package com.localmatters.serializer;

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

import com.localmatters.serializer.config.ComplexConfig;
import com.localmatters.serializer.config.Config;
import com.localmatters.serializer.config.IndexConfig;
import com.localmatters.serializer.config.MapConfig;
import com.localmatters.serializer.config.ValueConfig;
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>JSONFormatter</code>
 */
public class JSONSerializerTest extends TestCase {
	private JSONSerializer serializer;
	private SerializationContext ctx;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serializer = new JSONSerializer();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests the root serialization
	 */
	public void testRoot() throws Exception {
		Config config = createMock(Config.class);
		Object root = new Object();
		expect(config.getName()).andReturn("listing");
		expect(config.handle(root, ctx)).andReturn("{}");
		replay(config);
		String result = serializer.serializeRoot(config, root, ctx);
		verify(config);
		assertEquals("{\"listing\":{}}", result);
	}
	
	/**
	 * Tests serializing a null value that should not be written
	 */
	public void testValueWhenNullAndNotWrite() {
		ValueConfig config = createMock(ValueConfig.class);
		expect(config.isWriteEmpty()).andReturn(false);
		replay(config);
		String result = serializer.serializeValue(config, null, ctx);
		verify(config);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing a null value that should be written
	 */
	public void testValueWhenNullAndWrite() {
		ValueConfig config = createMock(ValueConfig.class);
		expect(config.isWriteEmpty()).andReturn(true);
		replay(config);
		String result = serializer.serializeValue(config, null, ctx);
		verify(config);
		assertEquals("null", result);
	}
	
	/**
	 * Tests serializing an empty value that should not be written
	 */
	public void testValueWhenEmptyAndNotWrite() {
		ValueConfig config = createMock(ValueConfig.class);
		expect(config.isWriteEmpty()).andReturn(false);
		replay(config);
		String result = serializer.serializeValue(config, StringUtils.EMPTY, ctx);
		verify(config);
		assertEquals(StringUtils.EMPTY, result);
	}
	
	/**
	 * Tests serializing an empty value that should be written
	 */
	public void testValueWhenEmptyAndWrite() {
		ValueConfig config = createMock(ValueConfig.class);
		expect(config.isWriteEmpty()).andReturn(true);
		replay(config);
		String result = serializer.serializeValue(config, StringUtils.EMPTY, ctx);
		verify(config);
		assertEquals("\"\"", result);
	}
	
	/**
	 * Tests serializing a number
	 */
	public void testValueWhenNumber() {
		ValueConfig config = createMock(ValueConfig.class);
		replay(config);
		String result = serializer.serializeValue(config, 123456, ctx);
		verify(config);
		assertEquals("123456", result);
	}
	
	/**
	 * Tests serializing a value
	 */
	public void testValue() {
		ValueConfig config = createMock(ValueConfig.class);
		replay(config);
		String result = serializer.serializeValue(config, "hotel & café", ctx);
		verify(config);
		assertEquals("\"hotel &amp; caf&#233;\"", result);
	}

	/**
	 * Tests serializing a null object that should not be written
	 */
	public void testComplexWhenNullAndNotWrite() throws Exception {
		Config config = createMock(ComplexConfig.class);
		expect(config.isWriteEmpty()).andReturn(false);
		replay(config);
		String result = serializer.serializeComplex(config, null, null, null, ctx);
		verify(config);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing a null object that should be written
	 */
	public void testComplexWhenNullAndWrite() throws Exception {
		Config config = createMock(ComplexConfig.class);
		expect(config.isWriteEmpty()).andReturn(true);
		replay(config);
		String result = serializer.serializeComplex(config, null, null, null, ctx);
		verify(config);
		assertEquals("null", result);
	}
	
	/**
	 * Tests serializing a object without properties
	 */
	public void testComplexWhenNoProperty() throws Exception {
		Config config = createMock(ComplexConfig.class);
		replay(config);
		String result = serializer.serializeComplex(config, null, null, new Object(), ctx);
		verify(config);
		assertEquals("{}", result);
	}
	
	/**
	 * Tests serializing a object for which all its properties are null
	 */
	public void testComplexWithOnlyNullProperties() throws Exception {
		Config config = createMock(ComplexConfig.class);
		Config attributeConfig = createMock(Config.class);
		Config elementConfig = createMock(Config.class);
		DummyObject listing = new DummyObject();
		
		expect(attributeConfig.handle(listing, ctx.appendSegment("listing"))).andReturn("");
		expect(elementConfig.handle(listing, ctx.appendSegment("listing"))).andReturn("");
		
		replay(config, attributeConfig, elementConfig);
		String result = serializer.serializeComplex(config, 
				CollectionUtils.asList(attributeConfig), 
				CollectionUtils.asList(elementConfig), 
				listing, ctx.appendSegment("listing"));
		verify(config, attributeConfig, elementConfig);
		assertEquals("{}", result);
	}
	
	/**
	 * Tests serializing a object
	 */
	public void testObject() throws Exception {
		Config config = createMock(ComplexConfig.class);
		Config attributeConfig = createMock(Config.class);
		Config elementConfig = createMock(Config.class);
		DummyObject listing = new DummyObject();
		listing.setId("123456");
		listing.setName("LocalMatters");
		
		expect(attributeConfig.getName()).andReturn("id");
		expect(attributeConfig.handle(listing, ctx.appendSegment("listing"))).andReturn("\"123456\"");
		expect(elementConfig.getName()).andReturn("name");
		expect(elementConfig.handle(listing, ctx.appendSegment("listing"))).andReturn("\"LocalMatters\"");
		
		replay(config, attributeConfig, elementConfig);
		String result = serializer.serializeComplex(config, 
				CollectionUtils.asList(attributeConfig), 
				CollectionUtils.asList(elementConfig), 
				listing, ctx.appendSegment("listing"));
		verify(config, attributeConfig, elementConfig);
		assertEquals("{\"id\":\"123456\",\"name\":\"LocalMatters\"}", result);
	}
	
	/**
	 * Tests serializing an attribute
	 */
	public void testAttribute() {
		Config config = createMock(Config.class);
		ValueConfig delegateConfig = createMock(ValueConfig.class);
		replay(config, delegateConfig);
		String result = serializer.serializeAttribute(config, "hotel & café", ctx);
		verify(config, delegateConfig);
		assertEquals("\"hotel &amp; caf&#233;\"", result);
	}

	/**
	 * Tests serializing an empty list that should not be written
	 */
	public void testListWhenEmptyAndNotWrite() throws Exception {
		Config config = createMock(IndexConfig.class);
		expect(config.isWriteEmpty()).andReturn(false);
		replay(config);
		String result = serializer.serializeIndex(config, null, Collections.EMPTY_LIST.iterator(), ctx);
		verify(config);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing an empty list that should  be written
	 */
	public void testListWhenEmptyAndWrite() throws Exception {
		Config config = createMock(IndexConfig.class);
		expect(config.isWriteEmpty()).andReturn(true);
		replay(config);
		String result = serializer.serializeIndex(config, null, Collections.EMPTY_LIST.iterator(), ctx);
		verify(config);
		assertEquals("[]", result);
	}

	/**
	 * Tests serializing a list that contains only null elements
	 */
	public void testListWhenContainsOnlyNullElements() throws Exception {
		Config config = createMock(IndexConfig.class);
		Config elementConfig = createMock(Config.class);
		Iterator<String> elements = CollectionUtils.asList("").iterator();
		
		expect(elementConfig.handle("", ctx.appendSegment("sports[0]"))).andReturn("");
		
		replay(config, elementConfig);
		String result = serializer.serializeIndex(config, elementConfig, elements, ctx.appendSegment("sports"));
		verify(config, elementConfig);
		assertEquals("[]", result);
	}

	/**
	 * Tests serializing a list
	 */
	public void testList() throws Exception {
		Config config = createMock(IndexConfig.class);
		Config elementConfig = createMock(Config.class);
		Iterator<String> elements = CollectionUtils.asList("baseball", "hockey").iterator();
		
		expect(elementConfig.handle("baseball", ctx.appendSegment("sports[0]"))).andReturn("\"baseball\"");
		expect(elementConfig.handle("hockey", ctx.appendSegment("sports[1]"))).andReturn("\"hockey\"");
		
		replay(config, elementConfig);
		String result = serializer.serializeIndex(config, elementConfig, elements, ctx.appendSegment("sports"));
		verify(config, elementConfig);
		assertEquals("[\"baseball\",\"hockey\"]", result);
	}


	/**
	 * Tests serializing an empty map that should not be written
	 */
	public void testMapWhenEmptyAndNotWrite() throws Exception {
		Config config = createMock(MapConfig.class);
		expect(config.isWriteEmpty()).andReturn(false);
		replay(config);
		String result = serializer.serializeMap(config, null, null, null, ctx);
		verify(config);
		assertEquals(StringUtils.EMPTY, result);
	}

	/**
	 * Tests serializing an empty map that should be written
	 */
	public void testMapWhenEmptyAndWrite() throws Exception {
		Config config = createMock(MapConfig.class);
		expect(config.isWriteEmpty()).andReturn(true);
		replay(config);
		String result = serializer.serializeMap(config, null, null, null, ctx);
		verify(config);
		assertEquals("{}", result);
	}

	/**
	 * Tests serializing a map
	 */
	public void testMap() throws Exception {
		Config config = createMock(MapConfig.class);
		Config keyConfig = createMock(Config.class);
		Config valueConfig = createMock(Config.class);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sport", "baskeball");
		map.put("hobby", "photography");
		
		expect(keyConfig.handle("sport", ctx.appendSegment("leisures{}"))).andReturn("\"sport\"");
		expect(valueConfig.handle("baskeball", ctx.appendSegment("leisures{\"sport\"}"))).andReturn("\"baskeball\"");
		expect(keyConfig.handle("hobby", ctx.appendSegment("leisures{}"))).andReturn("\"hobby\"");
		expect(valueConfig.handle("photography", ctx.appendSegment("leisures{\"hobby\"}"))).andReturn("\"photography\"");
		
		replay(config, keyConfig, valueConfig);
		String result = serializer.serializeMap(config, keyConfig, valueConfig, map, ctx.appendSegment("leisures"));
		verify(config, keyConfig, valueConfig);
		assertEquals("{\"sport\":\"baskeball\",\"hobby\":\"photography\"}", result);
	}
}
