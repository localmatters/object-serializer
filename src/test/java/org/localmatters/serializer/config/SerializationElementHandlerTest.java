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
package org.localmatters.serializer.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createMock;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_BEAN;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_CONSTANT;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_DISPLAY_EMPTY;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_ID;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_KEY;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_NAME;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PARENT;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PROPERTY;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_TARGET;
import static org.localmatters.serializer.config.SerializationElementHandler.DUPLICATE_ID_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_ATTRIBUTES_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_ATTRIBUTE_ELEMENT_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_LIST_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_LOOP_REFERENCES;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_MAP_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_NAMESPACE_ELEMENT_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.INVALID_TYPE_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.MISSING_ATTRIBUTE_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.MISSING_ID;
import static org.localmatters.serializer.config.SerializationElementHandler.NAME_NOT_ALLOWED_FORMAT;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_ATTRIBUTE;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_COMMENT;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_COMPLEX;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_LIST;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_MAP;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_NAMESPACE;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_REFERENCE;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_VALUE;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Element;
import org.localmatters.serializer.serialization.AttributeSerialization;
import org.localmatters.serializer.serialization.BeanSerialization;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.ConstantSerialization;
import org.localmatters.serializer.serialization.IteratorSerialization;
import org.localmatters.serializer.serialization.MapSerialization;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.NamespaceSerialization;
import org.localmatters.serializer.serialization.PropertySerialization;
import org.localmatters.serializer.serialization.ReferenceSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;


/**
 * Tests the <code>SerializationElementHandler</code>
 */
public class SerializationElementHandlerTest extends TestCase {
	private static final String PATH = "/serializations/complex/something";
	private SerializationElementHandler handler;
	private Element element;
	private Map<String, String> attributes;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		handler = new SerializationElementHandler();
		element = createMock(Element.class);
		attributes = new HashMap<String, String>();
	}
	
	/**
	 * Tests the <code>handleId</code> method when missing required ID
	 */
	public void testHandleIdWhenMissingRequiredId() {
		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		replay(element);
		try {
			handler.handleId(element, attributes, true);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(MISSING_ID, e.getMessage());
		}
		verify(element);
	}

	/**
	 * Tests the <code>handleId</code> method when duplicates
	 */
	public void testHandleIdWhenDuplicate() {
		ValueSerialization serialization = new ValueSerialization();
		handler.getSerializations().put("12345", serialization);

		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn("12345");
		replay(element);
		try {
			handler.handleId(element, attributes, false);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(DUPLICATE_ID_FORMAT, "12345"), e.getMessage());
		}
		verify(element);
	}
	
	/**
	 * Tests the <code>handleId</code> method
	 */
	public void testHandleId() {
		expect(element.attributeValue(ATTRIBUTE_ID)).andReturn("12345");
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList("12345"));

		replay(element);
		Serialization result = handler.handleId(element, attributes, true);
		verify(element);

		assertTrue(result instanceof ValueSerialization);
		assertEquals(1, CollectionUtils.size(handler.getSerializations()));
	}
	
	/**
	 * Tests the <code>handleName</code> method
	 */
	public void testHandleName() {
		expect(element.attributeValue(ATTRIBUTE_NAME)).andReturn("code");
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList("code"));

		replay(element);
		Serialization result = handler.handleName(element, attributes);
		verify(element);

		assertTrue(result instanceof NameSerialization);
		NameSerialization serialization =(NameSerialization) result;
		assertEquals("code", serialization.getName());
		assertTrue(serialization.getDelegate() instanceof ValueSerialization);
		ValueSerialization value = (ValueSerialization) serialization.getDelegate();
		assertEquals(value.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleConstant</code> method
	 */
	public void testHandleConstant() {
		expect(element.attributeValue(ATTRIBUTE_CONSTANT)).andReturn("abcd1234");
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList("abcd1234"));

		replay(element);
		Serialization result = handler.handleConstant(element, attributes);
		verify(element);

		assertTrue(result instanceof ConstantSerialization);
		ConstantSerialization serialization = (ConstantSerialization) result;
		assertEquals("abcd1234", serialization.getConstant());
        assertTrue(serialization.getDelegate() instanceof ValueSerialization);
        ValueSerialization value = (ValueSerialization) serialization.getDelegate();
		assertEquals(value.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleBean</code> method
	 */
	public void testHandleBean() {
		expect(element.attributeValue(ATTRIBUTE_BEAN)).andReturn("searchResults");
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList("searchResults"));

		replay(element);
		Serialization result = handler.handleBean(element, attributes);
		verify(element);

		assertTrue(result instanceof BeanSerialization);
		BeanSerialization serialization = (BeanSerialization) result;
		assertEquals("searchResults", serialization.getBean());
        assertTrue(serialization.getDelegate() instanceof ValueSerialization);
        ValueSerialization value = (ValueSerialization) serialization.getDelegate();
        assertEquals(value.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleProperty</code> method
	 */
	public void testHandleProperty() {
		expect(element.attributeValue(ATTRIBUTE_PROPERTY)).andReturn("address.street");
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList("address.street"));

		replay(element);
		Serialization result = handler.handleProperty(element, attributes);
		verify(element);

		assertTrue(result instanceof PropertySerialization);
		PropertySerialization serialization = (PropertySerialization) result;
		assertEquals("address.street", serialization.getProperty());
        assertTrue(serialization.getDelegate() instanceof ValueSerialization);
        ValueSerialization value = (ValueSerialization) serialization.getDelegate();
        assertEquals(value.isWriteEmpty(), serialization.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleValue</code> method
	 */
	public void testHandleValue() {
		replay(element);
		Serialization result = handler.handleValue(element, attributes);
		verify(element);
		assertTrue(result instanceof ValueSerialization);
	}

	/**
	 * Tests the <code>handleType</code> method when value
	 */
	public void testHandleTypeWhenValue() {
		expect(element.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("true"));

		replay(element);
		Serialization result = handler.handleType(element, attributes);
		verify(element);

        assertTrue(result instanceof ValueSerialization);
        ValueSerialization serialization = (ValueSerialization) result;
		assertTrue(serialization.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleAttribute</code> method when the element has no
	 * parent
	 */
	public void testHandleAttributeWhenNoParent() {
		expect(element.getParent()).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(element);
		try {
			handler.handleAttribute(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTE_ELEMENT_FORMAT, PATH), e.getMessage());
		}
		verify(element);
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
		replay(element, parent);
		try {
			handler.handleAttribute(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTE_ELEMENT_FORMAT, PATH), e.getMessage());
		}
		verify(element, parent);
	}

	/**
	 * Tests the <code>handleAttribute</code> method
	 */
	public void testHandleAttribute() {
		Element parent = createMock(Element.class);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		replay(element, parent);
		Serialization result = handler.handleAttribute(element, attributes);
		verify(element, parent);
		assertTrue(result instanceof AttributeSerialization);
	}
    
    /**
     * Tests the <code>handleType</code> method when attribute
     */
    public void testHandleTypeWhenAttribute() {
        Element parent = createMock(Element.class);
        expect(element.getName()).andReturn(TYPE_ATTRIBUTE);
        expect(element.getParent()).andReturn(parent);
        expect(parent.getName()).andReturn(TYPE_COMPLEX);
        expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("no");
        expect(element.attributes()).andReturn(Arrays.asList("no"));

        replay(element, parent);
        Serialization result = handler.handleType(element, attributes);
        verify(element, parent);

        assertTrue(result instanceof AttributeSerialization);
        assertFalse(result.isWriteEmpty());
    }

    /**
     * Tests the <code>handleNamespace</code> method when the element has no
     * parent
     */
    public void testHandleNamespaceWhenNoParent() {
        expect(element.getParent()).andReturn(null);
        expect(element.getPath()).andReturn(PATH);
        replay(element);
        try {
            handler.handleNamespace(element, attributes);
            fail("ConfigurationException expected");
        } catch (ConfigurationException e) {
            assertEquals(String.format(INVALID_NAMESPACE_ELEMENT_FORMAT, PATH), e.getMessage());
        }
        verify(element);
    }

    /**
     * Tests the <code>handleNamespace</code> method when the element's parent
     * is not a complex element
     */
    public void testHandleNamespaceWhenParentNotComplex() {
        Element parent = createMock(Element.class);
        expect(element.getParent()).andReturn(parent);
        expect(parent.getName()).andReturn(TYPE_LIST);
        expect(element.getPath()).andReturn(PATH);
        replay(element, parent);
        try {
            handler.handleNamespace(element, attributes);
            fail("ConfigurationException expected");
        } catch (ConfigurationException e) {
            assertEquals(String.format(INVALID_NAMESPACE_ELEMENT_FORMAT, PATH), e.getMessage());
        }
        verify(element, parent);
    }

    /**
     * Tests the <code>handleNamespace</code> method
     */
    public void handleNamespace() {
        Element parent = createMock(Element.class);
        expect(element.getParent()).andReturn(parent);
        expect(parent.getName()).andReturn(TYPE_COMPLEX);
        replay(element, parent);
        Serialization result = handler.handleNamespace(element, attributes);
        verify(element, parent);
        assertTrue(result instanceof NamespaceSerialization);
    }
	
	/**
	 * Tests the <code>handleType</code> method when name-space
	 */
	public void testHandleTypeWhenNamespace() {
		Element parent = createMock(Element.class);
		expect(element.getName()).andReturn(TYPE_NAMESPACE);
		expect(element.getParent()).andReturn(parent);
		expect(parent.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("no");
		expect(element.attributes()).andReturn(Arrays.asList("no"));

		replay(element, parent);
		Serialization result = handler.handleType(element, attributes);
		verify(element, parent);

		assertTrue(result instanceof NamespaceSerialization);
		assertFalse(result.isWriteEmpty());
	}

	/**
	 * Tests the <code>handleList</code> method when the element has no child
	 */
	public void testHandleListWhenNoChild() {
		expect(element.elements()).andReturn(null);

		replay(element);
		Serialization result = handler.handleList(element, attributes);
		verify(element);

		assertTrue(result instanceof IteratorSerialization);
		IteratorSerialization ser = (IteratorSerialization) result;
		assertTrue(ser.getElement() instanceof ValueSerialization);
		assertFalse(ser.getElement().isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser.getComments()));
	}

	/**
	 * Tests the <code>handleList</code> method when it has too many non comment
	 * children
	 */
	public void testHandleListWhenTooManyNonCommentChildren() {
		Element child1 = createMock(Element.class);
		Element child2 = createMock(Element.class);

		expect(element.elements()).andReturn(Arrays.asList(child1, child2));
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child1.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child1.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child1.attributes()).andReturn(Collections.emptyList());
		expect(child1.getName()).andReturn(TYPE_VALUE);
		expect(child2.getName()).andReturn(TYPE_VALUE);
		expect(element.getPath()).andReturn(PATH);

		replay(element, child1, child2);
		try {
			handler.handleList(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_LIST_FORMAT, PATH), e.getMessage());
		}
		verify(element, child1, child2);
	}

	/**
	 * Tests the <code>handleList</code> method
	 */
	public void testHandleList() {
		Element child = createMock(Element.class);
		Element comment = createMock(Element.class);

		expect(element.elements()).andReturn(Arrays.asList(child, comment));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello World");

		replay(element, child, comment);
		Serialization result = handler.handleList(element, attributes);
		verify(element, child, comment);

		assertTrue(result instanceof IteratorSerialization);
		IteratorSerialization ser = (IteratorSerialization) result;
		assertTrue(ser.getElement() instanceof ValueSerialization);
		assertFalse(ser.getElement().isWriteEmpty());
		assertEquals(1, CollectionUtils.size(ser.getComments()));
		assertEquals("Hello World", ser.getComments().get(0));
	}

	/**
	 * Tests the <code>handleType</code> method when list
	 */
	public void testHandleTypeWhenList() {
		Element child = createMock(Element.class);

		expect(element.getName()).andReturn(TYPE_LIST);
		expect(element.elements()).andReturn(Arrays.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("false"));

		replay(element, child);
		Serialization result = handler.handleType(element, attributes);
		verify(element, child);

        assertTrue(result instanceof IteratorSerialization);
        IteratorSerialization ser = (IteratorSerialization) result;
		assertTrue(ser.isWriteEmpty());
        assertTrue(ser.getElement() instanceof ValueSerialization);
        assertFalse(ser.getElement().isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser.getComments()));
	}
	
	/**
	 * Tests the <code>handleMap</code> method when it has too many non comment
	 * children
	 */
	public void testHandleMapWhenTooManyNonCommentChildren() {
		Element child1 = createMock(Element.class);
		Element child2 = createMock(Element.class);

		expect(element.elements()).andReturn(Arrays.asList(child1, child2));
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
		expect(child2.getName()).andReturn(TYPE_VALUE);
		expect(element.getPath()).andReturn(PATH);

		replay(element, child1, child2);
		try {
			handler.handleMap(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_MAP_FORMAT, PATH), e.getMessage());
		}
		verify(element, child1, child2);
	}
	
	/**
	 * Tests the <code>handleMap</code> method when the sub-element has an
	 * un-expected name 
	 */
	public void testHandleMapWhenSubElementHasName() {
		Element child = createMock(Element.class);

		expect(element.elements()).andReturn(Arrays.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn("aName");
		expect(element.getPath()).andReturn(PATH);

		replay(element, child);
		try {
			handler.handleMap(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(NAME_NOT_ALLOWED_FORMAT, PATH), e.getMessage());
		}
		verify(element, child);
	}
	
	/**
	 * Tests the <code>handleMap</code> method
	 */
	public void testHandleMap() {
		Element child = createMock(Element.class);

		expect(element.elements()).andReturn(Arrays.asList(child));
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(child.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(child.getName()).andReturn(TYPE_VALUE);
		expect(child.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(child.attributes()).andReturn(Collections.emptyList());
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn("listing.id");

		replay(element, child);
		Serialization result = handler.handleMap(element, attributes);
		verify(element, child);

		assertTrue(result instanceof MapSerialization);
		MapSerialization ser = (MapSerialization) result;
		assertSame("listing.id", ser.getKey());
		assertFalse(ser.isWriteEmpty());
		assertTrue(ser.getValue() instanceof ValueSerialization);
		assertFalse(ser.getValue().isWriteEmpty());
	}

	/**
	 * Tests the <code>handleType</code> method with an empty map
	 */
	public void testHandleTypeWhenEmptyMap() {
		expect(element.getName()).andReturn(TYPE_MAP);
		expect(element.elements()).andReturn(Collections.emptyList());
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("true"));

		replay(element);
		Serialization result = handler.handleType(element, attributes);
		verify(element);

        assertTrue(result instanceof MapSerialization);
        MapSerialization ser = (MapSerialization) result;
        assertTrue(ser.isWriteEmpty());
        assertNull(ser.getKey());
        assertTrue(ser.getValue() instanceof ValueSerialization);
		assertTrue(CollectionUtils.isEmpty(ser.getComments()));
	}

	/**
	 * Tests the <code>handleType</code> method with a map
	 */
	public void testHandleTypeWhenMap() {
		Element comment = createMock(Element.class);

		expect(element.getName()).andReturn(TYPE_MAP);
		expect(element.elements()).andReturn(Arrays.asList(comment));
		expect(comment.getName()).andReturn(TYPE_COMMENT);
		expect(comment.getStringValue()).andReturn("Hello Map");
		expect(element.attributeValue(ATTRIBUTE_KEY)).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("true"));

		replay(element, comment);
		Serialization result = handler.handleType(element, attributes);
		verify(element, comment);

		assertTrue(result instanceof MapSerialization);
		MapSerialization ser = (MapSerialization) result;
		assertTrue(ser.isWriteEmpty());
		assertNull(ser.getKey());
		assertTrue(ser.getValue() instanceof ValueSerialization);
		assertEquals(1, CollectionUtils.size(ser.getComments()));
		assertEquals("Hello Map", ser.getComments().get(0));
	}

	/**
	 * Tests the <code>handleReference</code> method when the target is missing
	 */
	public void testHandleReferenceWhenMissingTarget() {
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn(null);
		expect(element.getPath()).andReturn(PATH);
		replay(element);
		try {
			handler.handleReference(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_TARGET, PATH), e.getMessage());
		}
		verify(element);
	}

	/**
	 * Tests the <code>handleReference</code> method
	 */
	public void testHandleReference() {
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");

		replay(element);
		Serialization result = handler.handleReference(element, attributes);
		verify(element);

		assertTrue(result instanceof ReferenceSerialization);
		ReferenceSerialization serialization = (ReferenceSerialization) result;
		assertNull(serialization.getReferenced());
		assertEquals(1, CollectionUtils.size(handler.getReferences()));
		assertTrue(handler.getReferences().containsKey(serialization));
		assertEquals("12345", handler.getReferences().get(serialization));
	}

	/**
	 * Tests the <code>handleType</code> method when reference (invalid)
	 */
	public void testHandleTypeWhenReference() {
		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("12345", "true"));

		replay(element);
		Serialization result = handler.handleType(element, attributes);
		verify(element);

		assertTrue(result instanceof ReferenceSerialization);
		assertTrue(result.isWriteEmpty());
	}
	
	/**
	 * Tests the <code>handleComplex</code> method with name-space
	 */
	public void testHandleComplexWithNamespace() {
		Element namespace = createMock(Element.class);
		
		expect(element.elements()).andReturn(Arrays.asList(namespace));
		expect(namespace.getName()).andReturn(TYPE_NAMESPACE);
		expect(namespace.attributeValue(ATTRIBUTE_ID)).andReturn(null);
		expect(namespace.attributeValue(ATTRIBUTE_NAME)).andReturn(null);
		expect(namespace.attributeValue(ATTRIBUTE_CONSTANT)).andReturn(null);
		expect(namespace.attributeValue(ATTRIBUTE_BEAN)).andReturn(null);
		expect(namespace.attributeValue(ATTRIBUTE_PROPERTY)).andReturn(null);
		expect(namespace.getName()).andReturn(TYPE_NAMESPACE);
		expect(namespace.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(namespace.getParent()).andReturn(element);
		expect(namespace.attributes()).andReturn(Collections.emptyList());
		expect(element.getName()).andReturn(TYPE_COMPLEX);

		replay(element, namespace);
		Serialization result = handler.handleComplex(element, attributes);
		verify(element, namespace);

        assertTrue(result instanceof ComplexSerialization);
        ComplexSerialization ser = (ComplexSerialization) result;
		assertEquals(1, CollectionUtils.size(ser.getAttributes()));
		assertTrue(ser.getAttributes().get(0) instanceof NamespaceSerialization);
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
		assertTrue(CollectionUtils.isEmpty(ser.getComments()));
	}
    
    /**
     * Tests the <code>handleComplex</code> method
     */
    public void testHandleComplex() {
        Element attribute = createMock(Element.class);
        Element subElement = createMock(Element.class);
        Element comment = createMock(Element.class);
        
        expect(element.elements()).andReturn(Arrays.asList(attribute, comment, subElement));
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

        replay(element, attribute, comment, subElement);
        Serialization result = handler.handleComplex(element, attributes);
        verify(element, attribute, comment, subElement);

        assertTrue(result instanceof ComplexSerialization);
        ComplexSerialization ser = (ComplexSerialization) result;
        assertEquals(1, CollectionUtils.size(ser.getAttributes()));
        assertTrue(ser.getAttributes().get(0) instanceof AttributeSerialization);
        assertEquals(1, CollectionUtils.size(ser.getElements()));
        assertTrue(ser.getElements().get(0) instanceof ValueSerialization);
        assertEquals(1, CollectionUtils.size(ser.getComments()));
        assertEquals("Hello World", ser.getComments().get(0));
    }
	
	/**
	 * Tests the <code>HandleComplex</code> method when has ID and parent
	 */
	public void testHandleComplexWithIdAndParent() {
		attributes.put(ATTRIBUTE_ID, "54321");
		attributes.put(ATTRIBUTE_PARENT, "12345");

		expect(element.elements()).andReturn(null);

		replay(element);
		Serialization result = handler.handleComplex(element, attributes);
		verify(element);

        assertTrue(result instanceof ComplexSerialization);
        ComplexSerialization ser = (ComplexSerialization) result;
		assertTrue(CollectionUtils.isEmpty(ser.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
		assertEquals(1, CollectionUtils.size(handler.getComplexWithIds()));
		assertSame(ser, handler.getComplexWithIds().get("54321"));
		assertEquals(1, CollectionUtils.size(handler.getExtensions()));
		assertEquals("12345", handler.getExtensions().get(ser));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex and parent
	 */
	public void testHandleTypeWhenComplexAndParent() {
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn("parent");
		expect(element.elements()).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("parent", "true"));

		replay(element);
		Serialization result = handler.handleType(element, attributes);
		verify(element);

        assertTrue(result instanceof ComplexSerialization);
        ComplexSerialization ser = (ComplexSerialization) result;
		assertTrue(ser.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(ser.getElements()));
		assertEquals(1, CollectionUtils.size(handler.getExtensions()));
		assertEquals("parent", handler.getExtensions().get(ser));
	}
	
	/**
	 * Tests the <code>handleType</code> method when complex
	 */
	public void testHandleTypeWhenComplex() {
		expect(element.getName()).andReturn(TYPE_COMPLEX);
		expect(element.attributeValue(ATTRIBUTE_PARENT)).andReturn(null);
		expect(element.elements()).andReturn(null);
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn("true");
		expect(element.attributes()).andReturn(Arrays.asList("true"));

		replay(element);
		Serialization result = handler.handleType(element, attributes);
		verify(element);

		assertTrue(result instanceof ComplexSerialization);
		ComplexSerialization ser = (ComplexSerialization) result;
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
		replay(element);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_TYPE_FORMAT, "invalid", PATH), e.getMessage());
		}
		verify(element);
	}


	/**
	 * Tests the <code>handleType</code> method when extra attributes
	 */
	public void testHandleTypeWhenInvalidAttributes() {
		attributes.put(ATTRIBUTE_TARGET, "12345");
		Element target = createMock(Element.class);
		Element invalid = createMock(Element.class);

		expect(element.getName()).andReturn(TYPE_REFERENCE);
		expect(element.attributeValue(ATTRIBUTE_TARGET)).andReturn("12345");
		expect(element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY)).andReturn(null);
		expect(element.attributes()).andReturn(Arrays.asList(target, invalid));
		expect(element.attributes()).andReturn(Arrays.asList(target, invalid));
		expect(target.getName()).andReturn(ATTRIBUTE_TARGET);
		expect(invalid.getName()).andReturn("invalid-attribute");
		expect(element.getPath()).andReturn(PATH);

		replay(element, target, invalid);
		try {
			handler.handleType(element, attributes);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(String.format(INVALID_ATTRIBUTES_FORMAT, Arrays.asList("invalid-attribute"), TYPE_REFERENCE, PATH), e.getMessage());
		}
		verify(element, target, invalid);
	}
	
	/**
	 * Tests the resolution of the references when some are invalid
	 */
	public void testResolveInvalidReferences() {
		Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
		Map<String, Serialization> serializations = new HashMap<String, Serialization>();
		references.put(new ReferenceSerialization(), "12345");
		references.put(new ReferenceSerialization(), "6789");
		
		replay(element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, serializations);
		verify(element);
		
		assertEquals(2, CollectionUtils.size(results));
		assertTrue(results.containsAll(Arrays.asList("12345", "6789")));
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
		
		replay(element);
		Set<String> results = SerializationElementHandler.resolveReferences(references, serializations);
		verify(element);
		
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
		
		replay(element);
		Set<String> results = SerializationElementHandler.resolveExtensions(extensions, parents);
		verify(element);
		
		assertEquals(2, CollectionUtils.size(results));
		assertTrue(results.containsAll(Arrays.asList("12345", "6789")));
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
		
		replay(element);
		try {
			SerializationElementHandler.resolveExtensions(extensions, parents);
			fail("ConfigurationException expected");
		} catch (ConfigurationException e) {
			assertEquals(INVALID_LOOP_REFERENCES, e.getMessage());
		}
		verify(element);
	}
	
	/**
	 * Tests the resolution of the extensions
	 */
	public void testResolveExtensions() {
		ComplexSerialization ser1 = new ComplexSerialization();
		ser1.setWriteEmpty(Boolean.TRUE);
		Serialization ser1Attr = new AttributeSerialization();
		Serialization ser1Element = new ValueSerialization();
		ser1.setAttributes(Arrays.asList(ser1Attr));
		ser1.setElements(Arrays.asList(ser1Element));
		ComplexSerialization ser2 = new ComplexSerialization();
		ComplexSerialization ser3 = new ComplexSerialization();
		Serialization ser3Attr = new AttributeSerialization();
		ser3.setAttributes(Arrays.asList(ser3Attr));
		ComplexSerialization ser4 = new ComplexSerialization();
		Serialization ser4Element = new ValueSerialization();
		ser4.setWriteEmpty(Boolean.FALSE);
		ser4.setElements(Arrays.asList(ser4Element));
		Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>(); 
		extensions.put(ser1, "2");
		extensions.put(ser2, "3");
		extensions.put(ser3, "4");
		Map<String, ComplexSerialization> parents = new HashMap<String, ComplexSerialization>();
		parents.put("2", ser2);
		parents.put("3", ser3);
		parents.put("4", ser4);

		replay(element);
		Set<String> invalid = SerializationElementHandler.resolveExtensions(extensions, parents);
		verify(element);
		
		assertTrue(CollectionUtils.isEmpty(invalid));
		assertFalse(ser4.isWriteEmpty());
		assertTrue(CollectionUtils.isEmpty(ser4.getAttributes()));
		assertEquals(Arrays.asList(ser4Element), ser4.getElements());
		assertFalse(ser3.isWriteEmpty());
		assertEquals(Arrays.asList(ser3Attr), ser3.getAttributes());
		assertEquals(Arrays.asList(ser4Element), ser3.getElements());
		assertFalse(ser2.isWriteEmpty());
		assertEquals(Arrays.asList(ser3Attr), ser2.getAttributes());
		assertEquals(Arrays.asList(ser4Element), ser2.getElements());
		assertTrue(ser1.isWriteEmpty());
		assertEquals(Arrays.asList(ser3Attr, ser1Attr), ser1.getAttributes());
		assertEquals(Arrays.asList(ser4Element, ser1Element), ser1.getElements());
	}

}
