package com.localmatters.serializer.config;

import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_BEAN;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_CONSTANT;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_DISPLAY_EMPTY;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_ID;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_KEY;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_NAME;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PARENT;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PROPERTY;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_TARGET;
import static com.localmatters.serializer.config.SerializationElementHandler.DUPLICATE_ID_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_ATTRIBUTES_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_ATTRIBUTE_ELEMENT_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_LIST_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_LOOP_REFERENCES;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_MAP_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.INVALID_TYPE_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.MISSING_ATTRIBUTE_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.MISSING_ID;
import static com.localmatters.serializer.config.SerializationElementHandler.MISSING_NAME_OR_PARENT_FORMAT;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_ATTRIBUTE;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_COMMENT;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_COMPLEX;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_LIST;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_MAP;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_REFERENCE;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_VALUE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.dom4j.Element;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.objectfactory.LMObjectFactory;

/**
 * Tests the <code>SerializationElementHandler</code>
 */
public class SerializationElementHandlerTest extends TestCase {
	private static final String PATH = "/serializations/complex/something";
	private SerializationElementHandler handler;
	private LMObjectFactory factory;
	private Element element;
	private Map<String, String> attributes;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		factory = createMock(LMObjectFactory.class);
		handler = new SerializationElementHandler(factory);
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
		ValueSerialization serialization = new ValueSerialization();
		handler.getSerializations().put("12345", serialization);

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
		ValueSerialization serialization = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn("12345");
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("12345", "amount"));

		replay(factory, element);
		Serialization result = handler.handleId(element, attributes, true);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals(1, CollectionUtils.sizeOf(handler.getSerializations()));
	}
	
	/**
	 * Tests the <code>handleConstant</code> method
	 */
	public void testHandleConstant() {
		ConstantSerialization serialization = new ConstantSerialization();
		ValueSerialization valueConfig = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn("abcd1234");
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(ConstantSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("abcd1234", "amount"));

		replay(factory, element);
		Serialization result = handler.handleConstant(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("abcd1234", serialization.getConstant());
		assertSame(valueConfig, serialization.getDelegate());
		assertEquals(valueConfig.getName(), serialization.getName());
		assertEquals(valueConfig.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleBean</code> method
	 */
	public void testHandleBean() {
		BeanSerialization serialization = new BeanSerialization();
		ValueSerialization valueConfig = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn("searchResults");
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(BeanSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("searchResults", "amount"));

		replay(factory, element);
		Serialization result = handler.handleBean(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("searchResults", serialization.getBean());
		assertSame(valueConfig, serialization.getDelegate());
		assertEquals(valueConfig.getName(), serialization.getName());
		assertEquals(valueConfig.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleProperty</code> method
	 */
	public void testHandleProperty() {
		PropertySerialization serialization = new PropertySerialization();
		ValueSerialization valueConfig = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn("address.street");
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(PropertySerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("address.street", "amount"));

		replay(factory, element);
		Serialization result = handler.handleProperty(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("address.street", serialization.getProperty());
		assertSame(valueConfig, serialization.getDelegate());
		assertEquals(valueConfig.getName(), serialization.getName());
		assertEquals(valueConfig.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleValue</code> method
	 */
	public void testHandleValue() {
		ValueSerialization serialization = new ValueSerialization();
		expect(factory.create(ValueSerialization.class)).andReturn(serialization);
		replay(factory, element);
		Serialization result = handler.handleValue(element, attributes);
		verify(factory, element);
		assertSame(serialization, result);
	}

	/**
	 * Tests the <code>handleType</code> method when value
	 */
	public void testHandleTypeWhenValue() {
		ValueSerialization serialization = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("code", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("code", serialization.getName());
		assertTrue(serialization.isWriteEmpty());
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
		AttributeSerialization serialization = new AttributeSerialization();
		Element parent = createMock(Element.class);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeSerialization.class)).andReturn(serialization);
		replay(factory, element, parent);
		Serialization result = handler.handleAttribute(element, attributes);
		verify(factory, element, parent);
		assertSame(serialization, result);
	}

	/**
	 * Tests the <code>handleType</code> method when attribute
	 */
	public void testHandleTypeWhenAttribute() {
		AttributeSerialization serialization = new AttributeSerialization();
		Element parent = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("no");
		expect(element.attributes()).andReturn(CollectionUtils.asList("code", "no"));

		replay(factory, element, parent);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element, parent);

		assertSame(serialization, result);
		assertEquals("code", serialization.getName());
		assertFalse(serialization.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleList</code> method when the element has no child
	 */
	public void testHandleListWhenNoChild() {
		IteratorSerialization serialization = new IteratorSerialization();
		expect(factory.create(IteratorSerialization.class)).andReturn(serialization);
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
	 * Tests the <code>handleList</code> method when it has too many non comment
	 * children
	 */
	public void testHandleListWhenTooManyNonCommentChildren() {
		IteratorSerialization serialization = new IteratorSerialization();
		ValueSerialization elementConfig = new ValueSerialization();
		Element child1 = createMock(Element.class);
		Element child2 = createMock(Element.class);

		expect(factory.create(IteratorSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child1, child2));
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child1.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child1.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child1.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(elementConfig);
		expect(child2.getName()).andReturn(TYPE_VALUE);
		expect(element.getPath()).andReturn(PATH);

		replay(factory, element, child1, child2);
		try {
			handler.handleList(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_LIST_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element, child1, child2);
	}

	/**
	 * Tests the <code>handleList</code> method
	 */
	public void testHandleList() {
		IteratorSerialization serialization = new IteratorSerialization();
		ValueSerialization elementConfig = new ValueSerialization();
		Element child = createMock(Element.class);
		Element comment = createMock(Element.class);

		expect(factory.create(IteratorSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child, comment));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(elementConfig);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");

		replay(factory, element, child, comment);
		Serialization result = handler.handleList(element, attributes);
		verify(factory, element, child, comment);

		assertSame(serialization, result);
		assertSame(elementConfig, serialization.getElement());
		assertEquals("amount", elementConfig.getName());
		assertTrue(elementConfig.isWriteEmpty());
		assertEquals(1, CollectionUtils.sizeOf(serialization.getComments()));
		assertEquals("Hello World", serialization.getComments().get(0));
	}

	/**
	 * Tests the <code>handleType</code> method when list
	 */
	public void testHandleTypeWhenList() {
		IteratorSerialization serialization = new IteratorSerialization();
		ValueSerialization elementConfig = new ValueSerialization();
		Element child = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("orders");
		expect(element.getName()).andReturn(TYPE_LIST);
		expect(factory.create(IteratorSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(elementConfig);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("false");
		expect(element.attributes()).andReturn(CollectionUtils.asList("orders", "false"));

		replay(factory, element, child);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element, child);

		assertSame(serialization, result);
		assertEquals("orders", serialization.getName());
		assertFalse(serialization.isWriteEmpty());
		assertSame(elementConfig, serialization.getElement());
		assertEquals("amount", elementConfig.getName());
		assertTrue(elementConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleMap</code> method when the element has no child
	 */
	public void testHandleMapWhenNoChild() {
		MapSerialization serialization = new MapSerialization();
		expect(factory.create(MapSerialization.class)).andReturn(serialization);
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
	 * Tests the <code>handleMap</code> method when it has too many non comment
	 * children
	 */
	public void testHandleMapWhenTooManyNonCommentChildren() {
		MapSerialization serialization = new MapSerialization();
		ValueSerialization valueConfig = new ValueSerialization();
		Element child1 = createMock(Element.class);
		Element child2 = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child1, child2));
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child1.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child1.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child1.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(child2.getName()).andReturn(TYPE_VALUE);
		expect(element.getPath()).andReturn(PATH);

		replay(factory, element, child1, child2);
		try {
			handler.handleMap(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_MAP_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element, child1, child2);
	}

	/**
	 * Tests the <code>handleMap</code> method when the key attribute is not set
	 */
	public void testHandleMapWhenNoKey() {
		MapSerialization serialization = new MapSerialization();
		ValueSerialization keyConfig = new ValueSerialization();
		ValueSerialization valueConfig = new ValueSerialization();
		Element child = createMock(Element.class);
		Element comment = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child, comment));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(ValueSerialization.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn(null);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");

		replay(factory, element, child, comment);
		Serialization result = handler.handleMap(element, attributes);
		verify(factory, element, child, comment);

		assertSame(serialization, result);
		assertSame(keyConfig, serialization.getKey());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, serialization.getValue());
		assertEquals("amount", valueConfig.getName());
		assertTrue(valueConfig.isWriteEmpty());
		assertEquals(1, CollectionUtils.sizeOf(serialization.getComments()));
		assertEquals("Hello World", serialization.getComments().get(0));

	}
	
	/**
	 * Tests the <code>handleMap</code> method
	 */
	public void testHandleMap() {
		MapSerialization serialization = new MapSerialization();
		ValueSerialization keyConfig = new ValueSerialization();
		PropertySerialization propertyConfig = new PropertySerialization();
		ValueSerialization valueConfig = new ValueSerialization();
		Element child = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(serialization);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(ValueSerialization.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");
		expect(factory.create(PropertySerialization.class)).andReturn(propertyConfig);

		replay(factory, element, child);
		Serialization result = handler.handleMap(element, attributes);
		verify(factory, element, child);

		assertSame(serialization, result);
		assertSame(propertyConfig, serialization.getKey());
		assertNull(propertyConfig.getName());
		assertTrue(propertyConfig.isWriteEmpty());
		assertSame(keyConfig, propertyConfig.getDelegate());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, serialization.getValue());
		assertEquals("amount", valueConfig.getName());
		assertTrue(valueConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleType</code> method with a map
	 */
	public void testHandleTypeWhenMap() {
		MapSerialization serialization = new MapSerialization();
		ValueSerialization keyConfig = new ValueSerialization();
		PropertySerialization propertyConfig = new PropertySerialization();
		ValueSerialization valueConfig = new ValueSerialization();
		Element child = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("addresses");
		expect(element.getName()).andReturn(TYPE_MAP);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(ValueSerialization.class)).andReturn(keyConfig);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");
		expect(factory.create(PropertySerialization.class)).andReturn(propertyConfig);
		expect(factory.create(MapSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("addresses", "listing.id", "true"));

		replay(factory, element, child);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element, child);

		assertSame(serialization, result);
		assertEquals("addresses", serialization.getName());
		assertTrue(serialization.isWriteEmpty());
		assertSame(propertyConfig, serialization.getKey());
		assertNull(propertyConfig.getName());
		assertTrue(propertyConfig.isWriteEmpty());
		assertSame(keyConfig, propertyConfig.getDelegate());
		assertNull(keyConfig.getName());
		assertTrue(keyConfig.isWriteEmpty());
		assertSame(valueConfig, serialization.getValue());
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
		ReferenceSerialization serialization = new ReferenceSerialization();

		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceSerialization.class)).andReturn(serialization);

		replay(factory, element);
		Serialization result = handler.handleReference(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertNull(serialization.getReferenced());
		assertEquals(1, CollectionUtils.sizeOf(handler.getReferences()));
		assertTrue(handler.getReferences().containsKey(serialization));
		assertEquals("12345", handler.getReferences().get(serialization));
	}

	/**
	 * Tests the <code>handleType</code> method when reference (invalid)
	 */
	public void testHandleTypeWhenReference() {
		ReferenceSerialization serialization = new ReferenceSerialization();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "12345", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("listing", serialization.getName());
		assertTrue(serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleComplex</code> method
	 */
	public void testHandleComplex() {
		ComplexSerialization serialization = new ComplexSerialization();
		AttributeSerialization attributeConfig = new AttributeSerialization();
		ValueSerialization subElementConfig = new ValueSerialization();
		Element attribute = createMock(Element.class);
		Element subElement = createMock(Element.class);
		Element comment = createMock(Element.class);

		expect(element.elements()).andReturn(CollectionUtils.asList(attribute, comment, subElement));
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(attribute.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_NAME)).andReturn("amount");
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(attribute.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(attribute.getParent()).andReturn(element);
		expect(attribute.attributes()).andReturn(CollectionUtils.asList("amount"));
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeSerialization.class)).andReturn(attributeConfig);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(subElement.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_NAME)).andReturn("order");
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(subElement.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(subElement.attributes()).andReturn(CollectionUtils.asList("order"));
		expect(factory.create(ValueSerialization.class)).andReturn(subElementConfig);
		expect(factory.create(ComplexSerialization.class)).andReturn(serialization);
		
		replay(factory, element, attribute, comment, subElement);
		Serialization result = handler.handleComplex(element, attributes);
		verify(factory, element, attribute, comment, subElement);

		assertSame(serialization, result);
		assertEquals(1, CollectionUtils.sizeOf(serialization.getAttributes()));
		assertSame(attributeConfig, serialization.getAttributes().get(0));
		assertEquals(1, CollectionUtils.sizeOf(serialization.getElements()));
		assertSame(subElementConfig, serialization.getElements().get(0));
		assertEquals(1, CollectionUtils.sizeOf(serialization.getComments()));
		assertEquals("Hello World", serialization.getComments().get(0));
	}
	
	/**
	 * Tests the <code>HandleComplex</code> method when has ID and parent
	 */
	public void testHandleComplexWithIdAndParent() {
		ComplexSerialization serialization = new ComplexSerialization();
		attributes.put(ATTRIBUTE_ID, "54321");
		attributes.put(ATTRIBUTE_PARENT, "12345");

		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(serialization);

		replay(factory, element);
		Serialization result = handler.handleComplex(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertTrue(CollectionUtils.isEmpty(serialization.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(serialization.getElements()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getComplexWithIds()));
		assertSame(serialization, handler.getComplexWithIds().get("54321"));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("12345", handler.getExtensions().get(serialization));
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
		ComplexSerialization serialization = new ComplexSerialization();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn("parent");
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "parent", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("listing", serialization.getName());
		assertTrue(serialization.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(serialization.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(serialization.getElements()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("parent", handler.getExtensions().get(serialization));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex
	 */
	public void testHandleTypeWhenComplex() {
		ComplexSerialization serialization = new ComplexSerialization();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("listing");
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn(null);
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("listing", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("listing", serialization.getName());
		assertTrue(serialization.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(serialization.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(serialization.getElements()));
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
		ReferenceSerialization serialization = new ReferenceSerialization();
		attributes.put(ATTRIBUTE_TARGET, "12345");
		Element target = createMock(Element.class);
		Element invalid = createMock(Element.class);

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceSerialization.class)).andReturn(serialization);
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
		Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
		Map<String, Serialization> configs = new HashMap<String, Serialization>();
		references.put(new ReferenceSerialization(), "12345");
		references.put(new ReferenceSerialization(), "6789");
		
		replay(factory, element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, configs);
		verify(factory, element);
		
		assertEquals(2, CollectionUtils.sizeOf(results));
		assertTrue(results.containsAll(CollectionUtils.asSet("12345", "6789")));
	}
	
	/**
	 * Tests the resolution of the references
	 */
	public void testResolveReferences() {
		Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
		Map<String, Serialization> configs = new HashMap<String, Serialization>();
		ValueSerialization config1 = new ValueSerialization();
		ValueSerialization config2 = new ValueSerialization();
		ReferenceSerialization ref1 = new ReferenceSerialization();
		ReferenceSerialization ref2 = new ReferenceSerialization();
		configs.put("12345", config1);
		configs.put("6789", config2);
		references.put(ref1, "12345");
		references.put(ref2, "6789");
		
		replay(factory, element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, configs);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(results));
		assertSame(config1, ref1.getReferenced());
		assertSame(config2, ref2.getReferenced());
	}
	
	/**
	 * Tests the resolution of the extensions when some are invalid
	 */
	public void testResolveInvalidExtensions() {
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		extensions.put(new ComplexSerialization(), "12345");
		extensions.put(new ComplexSerialization(), "6789");
		
		replay(factory, element);
		Set<String> results = SerializationElementHandler.resolveExtensions(extensions, parents);
		verify(factory, element);
		
		assertEquals(2, CollectionUtils.sizeOf(results));
		assertTrue(results.containsAll(CollectionUtils.asSet("12345", "6789")));
	}
	
	/**
	 * Tests the resolution of the extensions when loop
	 */
	public void testResolveLoopingExtensions() {
		ComplexSerialization config1 = new ComplexSerialization();
		ComplexSerialization config2 = new ComplexSerialization();
		ComplexSerialization config3 = new ComplexSerialization();
		ComplexSerialization config4 = new ComplexSerialization();
		ComplexSerialization config5 = new ComplexSerialization();
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		extensions.put(config2, "3");
		extensions.put(config1, "2");
		extensions.put(config3, "1");
		extensions.put(config4, "5");
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		parents.put("1", config1);
		parents.put("2", config2);
		parents.put("3", config3);
		parents.put("5", config5);
		
		replay(factory, element);
		try {
			SerializationElementHandler.resolveExtensions(extensions, parents);
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
		ComplexSerialization config1 = new ComplexSerialization();
		config1.setName("config1");
		config1.setWriteEmpty(Boolean.TRUE);
		Serialization config1Attr = new AttributeSerialization();
		Serialization config1Ellement = new ValueSerialization();
		config1.setAttributes(CollectionUtils.asList(config1Attr));
		config1.setElements(CollectionUtils.asList(config1Ellement));
		ComplexSerialization config2 = new ComplexSerialization();
		ComplexSerialization config3 = new ComplexSerialization();
		Serialization config3Attr = new AttributeSerialization();
		config3.setAttributes(CollectionUtils.asList(config3Attr));
		ComplexSerialization config4 = new ComplexSerialization();
		Serialization config4Ellement = new ValueSerialization();
		config4.setName("config4");
		config4.setWriteEmpty(Boolean.FALSE);
		config4.setElements(CollectionUtils.asList(config4Ellement));
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		extensions.put(config1, "2");
		extensions.put(config2, "3");
		extensions.put(config3, "4");
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		parents.put("2", config2);
		parents.put("3", config3);
		parents.put("4", config4);

		replay(factory, element);
		Set<String> invalid = SerializationElementHandler.resolveExtensions(extensions, parents);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(invalid));
		assertEquals("config4", config4.getName());
		assertFalse(config4.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(config4.getAttributes()));
		assertEquals(CollectionUtils.asList(config4Ellement), config4.getElements());
		assertEquals("config4", config3.getName());
		assertFalse(config3.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr), config3.getAttributes());
		assertEquals(CollectionUtils.asList(config4Ellement), config3.getElements());
		assertEquals("config4", config2.getName());
		assertFalse(config2.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr), config2.getAttributes());
		assertEquals(CollectionUtils.asList(config4Ellement), config2.getElements());
		assertEquals("config1", config1.getName());
		assertTrue(config1.isWriteEmpty());
		assertEquals(CollectionUtils.asList(config3Attr, config1Attr), config1.getAttributes());
		assertEquals(CollectionUtils.asList(config4Ellement, config1Ellement), config1.getElements());
	}

}
