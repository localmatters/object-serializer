package com.localmatters.serializer.config;

import static com.localmatters.serializer.config.ConfigurationHandler.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.dom4j.Element;

import com.localmatters.serializer.config.AttributeConfig;
import com.localmatters.serializer.config.BeanConfig;
import com.localmatters.serializer.config.ComplexConfig;
import com.localmatters.serializer.config.Config;
import com.localmatters.serializer.config.ConfigurationException;
import com.localmatters.serializer.config.ConfigurationHandler;
import com.localmatters.serializer.config.IndexConfig;
import com.localmatters.serializer.config.MapConfig;
import com.localmatters.serializer.config.PropertyConfig;
import com.localmatters.serializer.config.ReferenceConfig;
import com.localmatters.serializer.config.ValueConfig;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.objectfactory.LMObjectFactory;

/**
 * Tests the <code>ConfigurationHandler</code>
 */
public class ConfigurationHandlerTest extends TestCase {
	private static final String PATH = "/config/bean/path";
	private ConfigurationHandler handler;
	private LMObjectFactory factory;
	private Element element;
	private Map<String, String> attributes;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		factory = createMock(LMObjectFactory.class);
		handler = new ConfigurationHandler(factory);
		element = createMock(Element.class);
		attributes = new HashMap<String, String>();
	}
	
	/**
	 * Tests the <code>handleId</code> method when missing required ID
	 */
	public void testHandleIdWhenMissingRequiredId() {
		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		replay(factory, element);
		try {
			handler.handleId(element, attributes, true);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(MISSING_ID, e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleId</code> method when duplicates
	 */
	public void testHandleIdWhenDuplicate() {
		ValueConfig config = new ValueConfig();
		handler.getConfigs().put("12345", config);

		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn("12345");
		replay(factory, element);
		try {
			handler.handleId(element, attributes, false);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(DUPLICATE_ID_FORMAT, "12345"), e.getMessage());
		}
		verify(factory, element);
	}
	
	/**
	 * Tests the <code>handleId</code> method
	 */
	public void testHandleId() {
		ValueConfig config = new ValueConfig();

		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn("12345");
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueConfig.class)).andReturn(config);
		expect(element.attributes()).andReturn(CollectionUtils.asList("12345", "amount"));

		replay(factory, element);
		Config result = handler.handleId(element, attributes, true);
		verify(factory, element);

		assertSame(config, result);
		assertEquals(1, CollectionUtils.sizeOf(handler.getConfigs()));
	}
	
	/**
	 * Tests the <code>handleBean</code> method
	 */
	public void testHandleBean() {
		BeanConfig config = new BeanConfig();
		ValueConfig valueConfig = new ValueConfig();

		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn("searchResults");
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueConfig.class)).andReturn(valueConfig);
		expect(factory.create(BeanConfig.class)).andReturn(config);
		expect(element.attributes()).andReturn(CollectionUtils.asList("searchResults", "amount"));

		replay(factory, element);
		Config result = handler.handleBean(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertSame(valueConfig, config.getDelegate());
		assertEquals(valueConfig.getName(), config.getName());
		assertEquals(valueConfig.isWriteEmpty(), config.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleProperty</code> method
	 */
	public void testHandleProperty() {
		PropertyConfig config = new PropertyConfig();
		ValueConfig valueConfig = new ValueConfig();

		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn("address.street");
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueConfig.class)).andReturn(valueConfig);
		expect(factory.create(PropertyConfig.class)).andReturn(config);
		expect(element.attributes()).andReturn(CollectionUtils.asList("address.street", "amount"));

		replay(factory, element);
		Config result = handler.handleProperty(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertSame(valueConfig, config.getDelegate());
		assertEquals(valueConfig.getName(), config.getName());
		assertEquals(valueConfig.isWriteEmpty(), config.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleValue</code> method
	 */
	public void testHandleValue() {
		ValueConfig config = new ValueConfig();
		expect(factory.create(ValueConfig.class)).andReturn(config);
		replay(factory, element);
		Config result = handler.handleValue(element, attributes);
		verify(factory, element);
		assertSame(config, result);
	}

	/**
	 * Tests the <code>handleType</code> method when value
	 */
	public void testHandleTypeWhenValue() {
		ValueConfig config = new ValueConfig();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("code", "true"));

		replay(factory, element);
		Config result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertEquals("code", config.getName());
		assertTrue(config.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleAttribute</code> method when the element has no
	 * parent
	 */
	public void testHandleAttributeWhenNoParent() {
		expect(element.getParent()).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleAttribute(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTE_ELEMENT_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleAttribute</code> method when the element's parent
	 * is not a complex element
	 */
	public void testHandleAttributeWhenParentNotComplex() {
		Element parent = createMock(Element.class);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_LIST);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element, parent);
		try {
			handler.handleAttribute(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTE_ELEMENT_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element, parent);
	}

	/**
	 * Tests the <code>handleAttribute</code> method
	 */
	public void testHandleAttribute() {
		AttributeConfig config = new AttributeConfig();
		Element parent = createMock(Element.class);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeConfig.class)).andReturn(config);
		replay(factory, element, parent);
		Config result = handler.handleAttribute(element, attributes);
		verify(factory, element, parent);
		assertSame(config, result);
	}

	/**
	 * Tests the <code>handleType</code> method when attribute
	 */
	public void testHandleTypeWhenAttribute() {
		AttributeConfig config = new AttributeConfig();
		Element parent = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("no");
		expect(element.attributes()).andReturn(CollectionUtils.asList("code", "no"));

		replay(factory, element, parent);
		Config result = handler.handleType(element, attributes);
		verify(factory, element, parent);

		assertSame(config, result);
		assertEquals("code", config.getName());
		assertFalse(config.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleList</code> method when the element has no child
	 */
	public void testHandleListWhenNoChild() {
		expect(element.elements()).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleList(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_LIST_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleList</code> method
	 */
	public void testHandleList() {
		IndexConfig config = new IndexConfig();
		ValueConfig elementConfig = new ValueConfig();
		Element child = createMock(Element.class);

		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueConfig.class)).andReturn(elementConfig);
		expect(factory.create(IndexConfig.class)).andReturn(config);

		replay(factory, element, child);
		Config result = handler.handleList(element, attributes);
		verify(factory, element, child);

		assertSame(config, result);
		assertSame(elementConfig, config.getElementConfig());
		assertEquals("amount", elementConfig.getName());
		assertTrue(elementConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleType</code> method when list
	 */
	public void testHandleTypeWhenList() {
		IndexConfig config = new IndexConfig();
		ValueConfig elementConfig = new ValueConfig();
		Element child = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("orders");
		expect(element.getName()).andReturn(TYPE_LIST);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueConfig.class)).andReturn(elementConfig);
		expect(factory.create(IndexConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("false");
		expect(element.attributes()).andReturn(CollectionUtils.asList("orders", "false"));

		replay(factory, element, child);
		Config result = handler.handleType(element, attributes);
		verify(factory, element, child);

		assertSame(config, result);
		assertEquals("orders", config.getName());
		assertFalse(config.isWriteEmpty());
		assertSame(elementConfig, config.getElementConfig());
		assertEquals("amount", elementConfig.getName());
		assertTrue(elementConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleMap</code> method when the element has no child
	 */
	public void testHandleMapWhenNoChild() {
		expect(element.elements()).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleMap(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_MAP_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleMap</code> method when the key attribute is not set
	 */
	public void testHandleMapWhenNoKey() {
		MapConfig config = new MapConfig();
		ValueConfig keyConfig = new ValueConfig();
		ValueConfig valueConfig = new ValueConfig();
		Element child = createMock(Element.class);

		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueConfig.class)).andReturn(valueConfig);
		expect(factory.create(ValueConfig.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn(null);
		expect(factory.create(MapConfig.class)).andReturn(config);

		replay(factory, element, child);
		Config result = handler.handleMap(element, attributes);
		verify(factory, element, child);

		assertSame(config, result);
		assertSame(keyConfig, config.getKeyConfig());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, config.getValueConfig());
		assertEquals("amount", valueConfig.getName());
		assertTrue(valueConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleMap</code> method
	 */
	public void testHandleMap() {
		MapConfig config = new MapConfig();
		ValueConfig keyConfig = new ValueConfig();
		PropertyConfig propertyConfig = new PropertyConfig();
		ValueConfig valueConfig = new ValueConfig();
		Element child = createMock(Element.class);

		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueConfig.class)).andReturn(valueConfig);
		expect(factory.create(ValueConfig.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");
		expect(factory.create(PropertyConfig.class)).andReturn(propertyConfig);
		expect(factory.create(MapConfig.class)).andReturn(config);

		replay(factory, element, child);
		Config result = handler.handleMap(element, attributes);
		verify(factory, element, child);

		assertSame(config, result);
		assertSame(propertyConfig, config.getKeyConfig());
		assertNull(propertyConfig.getName());
		assertTrue(propertyConfig.isWriteEmpty());
		assertSame(keyConfig, propertyConfig.getDelegate());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, config.getValueConfig());
		assertEquals("amount", valueConfig.getName());
		assertTrue(valueConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleType</code> method with a map
	 */
	public void testHandleTypeWhenMap() {
		MapConfig config = new MapConfig();
		ValueConfig keyConfig = new ValueConfig();
		PropertyConfig propertyConfig = new PropertyConfig();
		ValueConfig valueConfig = new ValueConfig();
		Element child = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("addresses");
		expect(element.getName()).andReturn(TYPE_MAP);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueConfig.class)).andReturn(valueConfig);
		expect(factory.create(ValueConfig.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");
		expect(factory.create(PropertyConfig.class)).andReturn(propertyConfig);
		expect(factory.create(MapConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("addresses", "listing.id", "true"));

		replay(factory, element, child);
		Config result = handler.handleType(element, attributes);
		verify(factory, element, child);

		assertSame(config, result);
		assertEquals("addresses", config.getName());
		assertTrue(config.isWriteEmpty());
		assertSame(propertyConfig, config.getKeyConfig());
		assertNull(propertyConfig.getName());
		assertTrue(propertyConfig.isWriteEmpty());
		assertSame(keyConfig, propertyConfig.getDelegate());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, config.getValueConfig());
		assertEquals("amount", valueConfig.getName());
		assertTrue(valueConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleReference</code> method when the target is missing
	 */
	public void testHandleReferenceWhenMissingTarget() {
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleReference(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_TARGET, PATH), e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleReference</code> method
	 */
	public void testHandleReference() {
		ReferenceConfig config = new ReferenceConfig();

		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceConfig.class)).andReturn(config);

		replay(factory, element);
		Config result = handler.handleReference(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertNull(config.getDelegate());
		assertEquals(1, CollectionUtils.sizeOf(handler.getReferences()));
		assertTrue(handler.getReferences().containsKey(config));
		assertEquals("12345", handler.getReferences().get(config));
	}

	/**
	 * Tests the <code>handleType</code> method when reference (invalid)
	 */
	public void testHandleTypeWhenReference() {
		ReferenceConfig config = new ReferenceConfig();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "12345", "true"));

		replay(factory, element);
		Config result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertEquals("listing", config.getName());
		assertTrue(config.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleComplex</code> method
	 */
	public void testHandleComplex() {
		ComplexConfig config = new ComplexConfig();
		AttributeConfig attributeConfig = new AttributeConfig();
		ValueConfig subElementConfig = new ValueConfig();
		Element attribute = createMock(Element.class);
		Element subElement = createMock(Element.class);

		expect(element.elements()).andReturn(CollectionUtils.asList(attribute, subElement));
		expect(attribute.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(attribute.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(attribute.getParent()).andReturn(element);
		expect(attribute.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeConfig.class)).andReturn(attributeConfig);
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(subElement.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_NAME)).andReturn("order");
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(subElement.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(subElement.attributes()).andReturn(CollectionUtils.asList("order"));
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueConfig.class)).andReturn(subElementConfig);
		expect(factory.create(ComplexConfig.class)).andReturn(config);
		
		replay(factory, element, attribute, subElement);
		Config result = handler.handleComplex(element, attributes);
		verify(factory, element, attribute, subElement);

		assertSame(config, result);
		assertEquals(1, CollectionUtils.sizeOf(config.getAttributeConfigs()));
		assertSame(attributeConfig, config.getAttributeConfigs().iterator().next());
		assertEquals(1, CollectionUtils.sizeOf(config.getElementConfigs()));
		assertSame(subElementConfig, config.getElementConfigs().iterator().next());
	}
	
	/**
	 * Tests the <code>HandleComplex</code> method when has ID and parent
	 */
	public void testHandleComplexWithIdAndParent() {
		ComplexConfig config = new ComplexConfig();
		attributes.put(ATTRIBUTE_ID, "54321");
		attributes.put(ATTRIBUTE_PARENT, "12345");

		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexConfig.class)).andReturn(config);

		replay(factory, element);
		Config result = handler.handleComplex(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertTrue(CollectionUtils.isEmpty(config.getAttributeConfigs()));
		assertTrue(CollectionUtils.isEmpty(config.getElementConfigs()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getComplexWithIds()));
		assertSame(config, handler.getComplexWithIds().get("54321"));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("12345", handler.getExtensions().get(config));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex that is missing its
	 * name and parent
	 */
	public void testHandleTypeWhenComplexMissingParentAndName() {
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(MISSING_NAME_OR_PARENT_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element);

	}
	
	/**
	 * Tests the <code>handleType</code> method when complex and parent
	 */
	public void testHandleTypeWhenComplexAndParent() {
		ComplexConfig config = new ComplexConfig();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn("parent");
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "parent", "true"));

		replay(factory, element);
		Config result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertEquals("listing", config.getName());
		assertTrue(config.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(config.getAttributeConfigs()));
		assertTrue(CollectionUtils.isEmpty(config.getElementConfigs()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("parent", handler.getExtensions().get(config));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex
	 */
	public void testHandleTypeWhenComplex() {
		ComplexConfig config = new ComplexConfig();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn(null);
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "true"));

		replay(factory, element);
		Config result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(config, result);
		assertEquals("listing", config.getName());
		assertTrue(config.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(config.getAttributeConfigs()));
		assertTrue(CollectionUtils.isEmpty(config.getElementConfigs()));
	}

	/**
	 * Tests the <code>handleType</code> method when name attribute is missing
	 */
	public void testHandleTypeWhenNameMissing() {
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.getPath()).andReturn(PATH);

		replay(factory, element);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_NAME, PATH), e.getMessage());
		}
		verify(factory, element);
	}

	/**
	 * Tests the <code>handleType</code> method when invalid
	 */
	public void testHandleTypeWhenInvalid() {
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn("invalid");
		expect(element.getPath()).andReturn(PATH);
		replay(factory, element);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_TYPE_FORMAT, "invalid", PATH), e.getMessage());
		}
		verify(factory, element);
	}


	/**
	 * Tests the <code>handleType</code> method when extra attributes
	 */
	public void testHandleTypeWhenInvalidAttributes() {
		ReferenceConfig config = new ReferenceConfig();
		attributes.put(ATTRIBUTE_TARGET, "12345");
		Element target = createMock(Element.class);
		Element invalid = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceConfig.class)).andReturn(config);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(CollectionUtils.asList(target, invalid));
		expect(element.attributes()).andReturn(CollectionUtils.asList(target, invalid));
		expect(target.getName()).andReturn(ATTRIBUTE_TARGET);
		expect(invalid.getName()).andReturn("invalid-attribute");
		expect(element.getPath()).andReturn(PATH);

		replay(factory, element, target, invalid);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTES_FORMAT, CollectionUtils.asList("invalid-attribute"), TYPE_REFERENCE, PATH), e.getMessage());
		}
		verify(factory, element, target, invalid);
	}
	
	/**
	 * Tests the resolution of the references when some are invalid
	 */
	public void testResolveInvalidReferences() {
		Map<ReferenceConfig, String> references = new HashMap<ReferenceConfig, String>();
		Map<String, Config> configs = new HashMap<String, Config>();
		references.put(new ReferenceConfig(), "12345");
		references.put(new ReferenceConfig(), "6789");
		
		replay(factory, element);
		Set<String> results = ConfigurationHandler.resolveReferences(references, configs);
		verify(factory, element);
		
		assertEquals(2, CollectionUtils.sizeOf(results));
		assertTrue(results.containsAll(CollectionUtils.asSet("12345", "6789")));
	}
	
	/**
	 * Tests the resolution of the references
	 */
	public void testResolveReferences() {
		Map<ReferenceConfig, String> references = new HashMap<ReferenceConfig, String>();
		Map<String, Config> configs = new HashMap<String, Config>();
		ValueConfig config1 = new ValueConfig();
		ValueConfig config2 = new ValueConfig();
		ReferenceConfig ref1 = new ReferenceConfig();
		ReferenceConfig ref2 = new ReferenceConfig();
		configs.put("12345", config1);
		configs.put("6789", config2);
		references.put(ref1, "12345");
		references.put(ref2, "6789");
		
		replay(factory, element);
		Set<String> results = ConfigurationHandler.resolveReferences(references, configs);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(results));
		assertSame(config1, ref1.getDelegate());
		assertSame(config2, ref2.getDelegate());
	}
	
	/**
	 * Tests the resolution of the extensions when some are invalid
	 */
	public void testResolveInvalidExtensions() {
		Map<ComplexConfig, String> extensions = new HashMap<ComplexConfig, String>(); 
		Map<String, ComplexConfig> parents = new HashMap<String, ComplexConfig>();
		extensions.put(new ComplexConfig(), "12345");
		extensions.put(new ComplexConfig(), "6789");
		
		replay(factory, element);
		Set<String> results = ConfigurationHandler.resolveExtensions(extensions, parents);
		verify(factory, element);
		
		assertEquals(2, CollectionUtils.sizeOf(results));
		assertTrue(results.containsAll(CollectionUtils.asSet("12345", "6789")));
	}
	
	/**
	 * Tests the resolution of the extensions when loop
	 */
	public void testResolveLoopingExtensions() {
		ComplexConfig config1 = new ComplexConfig();
		ComplexConfig config2 = new ComplexConfig();
		ComplexConfig config3 = new ComplexConfig();
		ComplexConfig config4 = new ComplexConfig();
		ComplexConfig config5 = new ComplexConfig();
		Map<ComplexConfig, String> extensions = new HashMap<ComplexConfig, String>(); 
		extensions.put(config2, "3");
		extensions.put(config1, "2");
		extensions.put(config3, "1");
		extensions.put(config4, "5");
		Map<String, ComplexConfig> parents = new HashMap<String, ComplexConfig>();
		parents.put("1", config1);
		parents.put("2", config2);
		parents.put("3", config3);
		parents.put("5", config5);
		
		replay(factory, element);
		try {
			ConfigurationHandler.resolveExtensions(extensions, parents);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(INVALID_LOOP_REFERENCES, e.getMessage());
		}
		verify(factory, element);
	}
	
	/**
	 * Tests the resolution of the extensions
	 */
	public void testResolveExtensions() {
		ComplexConfig config1 = new ComplexConfig();
		config1.setName("config1");
		config1.setWriteEmpty(Boolean.TRUE);
		Config config1Attr = new AttributeConfig();
		Config config1Ellement = new ValueConfig();
		config1.setAttributeConfigs(CollectionUtils.asList(config1Attr));
		config1.setElementConfigs(CollectionUtils.asList(config1Ellement));
		ComplexConfig config2 = new ComplexConfig();
		ComplexConfig config3 = new ComplexConfig();
		Config config3Attr = new AttributeConfig();
		config3.setAttributeConfigs(CollectionUtils.asList(config3Attr));
		ComplexConfig config4 = new ComplexConfig();
		Config config4Ellement = new ValueConfig();
		config4.setName("config4");
		config4.setWriteEmpty(Boolean.FALSE);
		config4.setElementConfigs(CollectionUtils.asList(config4Ellement));
		Map<ComplexConfig, String> extensions = new HashMap<ComplexConfig, String>(); 
		extensions.put(config1, "2");
		extensions.put(config2, "3");
		extensions.put(config3, "4");
		Map<String, ComplexConfig> parents = new HashMap<String, ComplexConfig>();
		parents.put("2", config2);
		parents.put("3", config3);
		parents.put("4", config4);

		replay(factory, element);
		Set<String> invalid = ConfigurationHandler.resolveExtensions(extensions, parents);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(invalid));
		assertEquals("config4", config4.getName());
		assertFalse(config4.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(config4.getAttributeConfigs()));
		assertEquals(CollectionUtils.asList(config4Ellement), config4.getElementConfigs());
		assertEquals("config4", config3.getName());
		assertFalse(config3.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr), config3.getAttributeConfigs());
		assertEquals(CollectionUtils.asList(config4Ellement), config3.getElementConfigs());
		assertEquals("config4", config2.getName());
		assertFalse(config2.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr), config2.getAttributeConfigs());
		assertEquals(CollectionUtils.asList(config4Ellement), config2.getElementConfigs());
		assertEquals("config1", config1.getName());
		assertTrue(config1.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr, config1Attr), config1.getAttributeConfigs());
		assertEquals(CollectionUtils.asList(config4Ellement, config1Ellement), config1.getElementConfigs());
	}

}
