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
import static com.localmatters.serializer.config.SerializationElementHandler.NAME_NOT_ALLOWED_FORMAT;
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

import java.util.Collections;
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
import com.localmatters.serializer.serialization.NameSerialization;
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
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("12345"));

		replay(factory, element);
		Serialization result = handler.handleId(element, attributes, true);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals(1, CollectionUtils.sizeOf(handler.getSerializations()));
	}
	
	/**
	 * Tests the <code>handleName</code> method
	 */
	public void testHandleName() {
		NameSerialization serialization = new NameSerialization();
		ValueSerialization valueConfig = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(NameSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("code"));

		replay(factory, element);
		Serialization result = handler.handleName(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("code", serialization.getName());
		assertSame(valueConfig, serialization.getDelegate());
		assertEquals(valueConfig.isWriteEmpty(), serialization.isWriteEmpty());
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
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(ConstantSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("abcd1234"));

		replay(factory, element);
		Serialization result = handler.handleConstant(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("abcd1234", serialization.getConstant());
		assertSame(valueConfig, serialization.getDelegate());
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
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(BeanSerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("searchResults"));

		replay(factory, element);
		Serialization result = handler.handleBean(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("searchResults", serialization.getBean());
		assertSame(valueConfig, serialization.getDelegate());
		assertEquals(valueConfig.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleProperty</code> method
	 */
	public void testHandleProperty() {
		PropertySerialization serialization = new PropertySerialization();
		ValueSerialization valueConfig = new ValueSerialization();

		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn("address.street");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(factory.create(PropertySerialization.class)).andReturn(serialization);
		expect(element.attributes()).andReturn(CollectionUtils.asList("address.street"));

		replay(factory, element);
		Serialization result = handler.handleProperty(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertEquals("address.street", serialization.getProperty());
		assertSame(valueConfig, serialization.getDelegate());
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

		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
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

		expect(element.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("no");
		expect(element.attributes()).andReturn(CollectionUtils.asList("no"));

		replay(factory, element, parent);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element, parent);

		assertSame(serialization, result);
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
		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child1.attributes()).andReturn(Collections.emptyList());
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
		IteratorSerialization ser = new IteratorSerialization();
		ValueSerialization elementSer = new ValueSerialization();
		Element child = createMock(Element.class);
		Element comment = createMock(Element.class);

		expect(factory.create(IteratorSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(CollectionUtils.asList(child, comment));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(elementSer);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");

		replay(factory, element, child, comment);
		Serialization result = handler.handleList(element, attributes);
		verify(factory, element, child, comment);

		assertSame(ser, result);
		assertSame(elementSer, ser.getElement());
		assertFalse(elementSer.isWriteEmpty());
		assertEquals(1, CollectionUtils.sizeOf(ser.getComments()));
		assertEquals("Hello World", ser.getComments().get(0));
	}

	/**
	 * Tests the <code>handleType</code> method when list
	 */
	public void testHandleTypeWhenList() {
		IteratorSerialization ser = new IteratorSerialization();
		ValueSerialization elementSer = new ValueSerialization();
		Element child = createMock(Element.class);

		expect(element.getName()).andReturn(TYPE_LIST);
		expect(factory.create(IteratorSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(elementSer);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("false"));

		replay(factory, element, child);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element, child);

		assertSame(ser, result);
		assertTrue(ser.isWriteEmpty());
		assertSame(elementSer, ser.getElement());
		assertFalse(elementSer.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleMap</code> method when it has too many non comment
	 * children
	 */
	public void testHandleMapWhenTooManyNonCommentChildren() {
		MapSerialization ser = new MapSerialization();
		ValueSerialization valueSer = new ValueSerialization();
		Element child1 = createMock(Element.class);
		Element child2 = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(CollectionUtils.asList(child1, child2));
		expect(child1.getName()).andReturn(TYPE_VALUE);

		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child1.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child1.attributes()).andReturn(Collections.emptyList());
		expect(factory.create(ValueSerialization.class)).andReturn(valueSer);
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
	 * Tests the <code>handleMap</code> method when the sub-element has an
	 * un-expected name 
	 */
	public void testHandleMapWhenSubElementHasName() {
		MapSerialization ser = new MapSerialization();
		Element child = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("aName");
		expect(element.getPath()).andReturn(PATH);

		replay(factory, element, child);
		try {
			handler.handleMap(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(NAME_NOT_ALLOWED_FORMAT, PATH), e.getMessage());
		}
		verify(factory, element, child);
	}
	
	/**
	 * Tests the <code>handleMap</code> method
	 */
	public void testHandleMap() {
		MapSerialization ser = new MapSerialization();
		ValueSerialization valueConfig = new ValueSerialization();
		Element child = createMock(Element.class);

		expect(factory.create(MapSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(CollectionUtils.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(factory.create(ValueSerialization.class)).andReturn(valueConfig);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());

		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");

		replay(factory, element, child);
		Serialization result = handler.handleMap(element, attributes);
		verify(factory, element, child);

		assertSame(ser, result);
		assertSame("listing.id", ser.getKey());
		assertFalse(ser.isWriteEmpty());
		assertSame(valueConfig, ser.getValue());
		assertFalse(valueConfig.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleType</code> method with a map
	 */
	public void testHandleTypeWhenMap() {
		MapSerialization ser = new MapSerialization();
		ValueSerialization valueSer = new ValueSerialization();

		expect(element.getName()).andReturn(TYPE_MAP);
		expect(factory.create(MapSerialization.class)).andReturn(ser);
		expect(element.elements()).andReturn(Collections.emptyList());
		expect(factory.create(ValueSerialization.class)).andReturn(valueSer);
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(ser, result);
		assertTrue(ser.isWriteEmpty());
		assertNull(ser.getKey());
		assertSame(valueSer, ser.getValue());
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

		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(factory.create(ReferenceSerialization.class)).andReturn(serialization);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("12345", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(serialization, result);
		assertTrue(serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleComplex</code> method
	 */
	public void testHandleComplex() {
		ComplexSerialization ser = new ComplexSerialization();
		AttributeSerialization attributeSer = new AttributeSerialization();
		ValueSerialization subElementSer = new ValueSerialization();
		Element attribute = createMock(Element.class);
		Element subElement = createMock(Element.class);
		Element comment = createMock(Element.class);
		
		expect(element.elements()).andReturn(CollectionUtils.asList(attribute, comment, subElement));
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(attribute.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(attribute.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(attribute.getName()).andReturn(TYPE_ATTRIBUTE);
		expect(attribute.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(attribute.getParent()).andReturn(element);
		expect(attribute.attributes()).andReturn(Collections.emptyList());
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(factory.create(AttributeSerialization.class)).andReturn(attributeSer);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(subElement.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(subElement.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(subElement.getName()).andReturn(TYPE_VALUE);
		expect(subElement.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(subElement.attributes()).andReturn(Collections.emptyList());
		expect(factory.create(ValueSerialization.class)).andReturn(subElementSer);
		
		expect(factory.create(ComplexSerialization.class)).andReturn(ser);

		replay(factory, element, attribute, comment, subElement);
		Serialization result = handler.handleComplex(element, attributes);
		verify(factory, element, attribute, comment, subElement);

		assertSame(ser, result);
		assertEquals(1, CollectionUtils.sizeOf(ser.getAttributes()));
		assertSame(attributeSer, ser.getAttributes().get(0));
		assertEquals(1, CollectionUtils.sizeOf(ser.getElements()));
		assertSame(subElementSer, ser.getElements().get(0));
		assertEquals(1, CollectionUtils.sizeOf(ser.getComments()));
		assertEquals("Hello World", ser.getComments().get(0));
	}
	
	/**
	 * Tests the <code>HandleComplex</code> method when has ID and parent
	 */
	public void testHandleComplexWithIdAndParent() {
		ComplexSerialization ser = new ComplexSerialization();
		attributes.put(ATTRIBUTE_ID, "54321");
		attributes.put(ATTRIBUTE_PARENT, "12345");

		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(ser);

		replay(factory, element);
		Serialization result = handler.handleComplex(element, attributes);
		verify(factory, element);

		assertSame(ser, result);
		assertTrue(CollectionUtils.isEmpty(ser.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getComplexWithIds()));
		assertSame(ser, handler.getComplexWithIds().get("54321"));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("12345", handler.getExtensions().get(ser));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex and parent
	 */
	public void testHandleTypeWhenComplexAndParent() {
		ComplexSerialization ser = new ComplexSerialization();

		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn("parent");
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(ser);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("parent", "true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(ser, result);
		assertTrue(ser.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
		assertEquals(1, CollectionUtils.sizeOf(handler.getExtensions()));
		assertEquals("parent", handler.getExtensions().get(ser));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex
	 */
	public void testHandleTypeWhenComplex() {
		ComplexSerialization ser = new ComplexSerialization();

		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn(null);
		expect(element.elements()).andReturn(null);
		expect(factory.create(ComplexSerialization.class)).andReturn(ser);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(CollectionUtils.asList("true"));

		replay(factory, element);
		Serialization result = handler.handleType(element, attributes);
		verify(factory, element);

		assertSame(ser, result);
		assertTrue(ser.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
	}

	/**
	 * Tests the <code>handleType</code> method when invalid
	 */
	public void testHandleTypeWhenInvalid() {
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
		Map<String, Serialization> serializations = new HashMap<String, Serialization>();
		references.put(new ReferenceSerialization(), "12345");
		references.put(new ReferenceSerialization(), "6789");
		
		replay(factory, element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, serializations);
		verify(factory, element);
		
		assertEquals(2, CollectionUtils.sizeOf(results));
		assertTrue(results.containsAll(CollectionUtils.asSet("12345", "6789")));
	}
	
	/**
	 * Tests the resolution of the references
	 */
	public void testResolveReferences() {
		Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
		Map<String, Serialization> serializations = new HashMap<String, Serialization>();
		ValueSerialization ser1 = new ValueSerialization();
		ValueSerialization ser2 = new ValueSerialization();
		ReferenceSerialization ref1 = new ReferenceSerialization();
		ReferenceSerialization ref2 = new ReferenceSerialization();
		serializations.put("12345", ser1);
		serializations.put("6789", ser2);
		references.put(ref1, "12345");
		references.put(ref2, "6789");
		
		replay(factory, element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, serializations);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(results));
		assertSame(ser1, ref1.getReferenced());
		assertSame(ser2, ref2.getReferenced());
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
		ComplexSerialization ser1 = new ComplexSerialization();
		ComplexSerialization ser2 = new ComplexSerialization();
		ComplexSerialization ser3 = new ComplexSerialization();
		ComplexSerialization ser4 = new ComplexSerialization();
		ComplexSerialization ser5 = new ComplexSerialization();
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		extensions.put(ser2, "3");
		extensions.put(ser1, "2");
		extensions.put(ser3, "1");
		extensions.put(ser4, "5");
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		parents.put("1", ser1);
		parents.put("2", ser2);
		parents.put("3", ser3);
		parents.put("5", ser5);
		
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
		ComplexSerialization ser1 = new ComplexSerialization();
		ser1.setWriteEmpty(Boolean.TRUE);
		Serialization ser1Attr = new AttributeSerialization();
		Serialization ser1Element = new ValueSerialization();
		ser1.setAttributes(CollectionUtils.asList(ser1Attr));
		ser1.setElements(CollectionUtils.asList(ser1Element));
		ComplexSerialization ser2 = new ComplexSerialization();
		ComplexSerialization ser3 = new ComplexSerialization();
		Serialization ser3Attr = new AttributeSerialization();
		ser3.setAttributes(CollectionUtils.asList(ser3Attr));
		ComplexSerialization ser4 = new ComplexSerialization();
		Serialization ser4Element = new ValueSerialization();
		ser4.setWriteEmpty(Boolean.FALSE);
		ser4.setElements(CollectionUtils.asList(ser4Element));
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		extensions.put(ser1, "2");
		extensions.put(ser2, "3");
		extensions.put(ser3, "4");
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		parents.put("2", ser2);
		parents.put("3", ser3);
		parents.put("4", ser4);

		replay(factory, element);
		Set<String> invalid = SerializationElementHandler.resolveExtensions(extensions, parents);
		verify(factory, element);
		
		assertTrue(CollectionUtils.isEmpty(invalid));
		assertFalse(ser4.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser4.getAttributes()));
		assertEquals(CollectionUtils.asList(ser4Element), ser4.getElements());
		assertFalse(ser3.isWriteEmpty());
		assertEquals(CollectionUtils.asList(ser3Attr), ser3.getAttributes());
		assertEquals(CollectionUtils.asList(ser4Element), ser3.getElements());
		assertFalse(ser2.isWriteEmpty());
		assertEquals(CollectionUtils.asList(ser3Attr), ser2.getAttributes());
		assertEquals(CollectionUtils.asList(ser4Element), ser2.getElements());
		assertTrue(ser1.isWriteEmpty());
		assertEquals(CollectionUtils.asList(ser3Attr, ser1Attr), ser1.getAttributes());
		assertEquals(CollectionUtils.asList(ser4Element, ser1Element), ser1.getElements());
	}

}
