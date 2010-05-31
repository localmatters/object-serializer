/**
 * 
 */
package com.localmatters.serializer.util;

import junit.framework.TestCase;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>SerializationUtils</code>
 */
public class SerializationUtilsTest extends TestCase {

	/**
	 * Tests the instantiation of this class (for code completion)
	 */
	public void testInstantiation() {
		assertNotNull(new SerializationUtils() {});
	}

	/**
	 * Tests creating a new name attribute serialization
	 */
	public void testCreateAttributeWithName() {
		NameSerialization name = SerializationUtils.createAttribute("name");
		assertEquals("name", name.getName());
		assertTrue(name.getDelegate() instanceof AttributeSerialization);
	}

	/**
	 * Tests creating a new name property attribute serialization
	 */
	public void testCreateAttributeWithNameAndProperty() {
		NameSerialization name = SerializationUtils.createAttribute("name", "property");
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		assertTrue(delegate instanceof PropertySerialization);
		PropertySerialization property = (PropertySerialization) delegate;
		assertEquals("property", property.getProperty());
		assertTrue(property.getDelegate() instanceof AttributeSerialization);
	}

	/**
	 * Tests creating a new property attribute serialization
	 */
	public void testCreatePropertyAttribute() {
		PropertySerialization property = SerializationUtils.createPropertyAttribute("property");
		assertEquals("property", property.getProperty());
		assertTrue(property.getDelegate() instanceof AttributeSerialization);
	}

	/**
	 * Tests creating a new constant attribute serialization
	 */
	public void testCreateConstantAttribute() {
		NameSerialization name = SerializationUtils.createConstantAttribute("name", "constant");
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		assertTrue(delegate instanceof ConstantSerialization);
		ConstantSerialization constant = (ConstantSerialization) delegate;
		assertEquals("constant", constant.getConstant());
		assertTrue(constant.getDelegate() instanceof AttributeSerialization);
	}

	/**
	 * Tests creating a new name value serialization
	 */
	public void testCreateValueWithName() {
		NameSerialization name = SerializationUtils.createValue("name");
		assertEquals("name", name.getName());
		assertTrue(name.getDelegate() instanceof ValueSerialization);
	}

	/**
	 * Tests creating a new name property value serialization
	 */
	public void testCreateValueWithNameAndProperty() {
		NameSerialization name = SerializationUtils.createValue("name", "property");
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		assertTrue(delegate instanceof PropertySerialization);
		PropertySerialization property = (PropertySerialization) delegate;
		assertEquals("property", property.getProperty());
		assertTrue(property.getDelegate() instanceof ValueSerialization);
	}

	/**
	 * Tests creating a new property value serialization
	 */
	public void testCreatePropertyValue() {
		PropertySerialization property = SerializationUtils.createPropertyValue("property");
		assertEquals("property", property.getProperty());
		assertTrue(property.getDelegate() instanceof ValueSerialization);
	}

	/**
	 * Tests creating a new constant value serialization
	 */
	public void testCreateConstantValue() {
		NameSerialization name = SerializationUtils.createConstantValue("name", "constant");
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		assertTrue(delegate instanceof ConstantSerialization);
		ConstantSerialization constant = (ConstantSerialization) delegate;
		assertEquals("constant", constant.getConstant());
		assertTrue(constant.getDelegate() instanceof ValueSerialization);
	}

	/**
	 * Tests creating a new complex serialization with attributes
	 */
	public void testCreateComplexWithAttributes() {
		Serialization attr1 = SerializationUtils.createAttribute("attr1");
		Serialization attr2 = SerializationUtils.createAttribute("attr2");
		NameSerialization name = SerializationUtils.createComplexWithAttributes("name", attr1, attr2);
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		ComplexSerialization complex = (ComplexSerialization) delegate;
		assertEquals(2, CollectionUtils.sizeOf(complex.getAttributes()));
		assertSame(attr1, complex.getAttributes().get(0));
		assertSame(attr2, complex.getAttributes().get(1));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
	}

	/**
	 * Tests creating a new complex serialization with elements
	 */
	public void testCreateComplexWithElements() {
		Serialization element1 = SerializationUtils.createValue("element1");
		Serialization element2 = SerializationUtils.createValue("element2");
		NameSerialization name = SerializationUtils.createComplexWithElements("name", element1, element2);
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		ComplexSerialization complex = (ComplexSerialization) delegate;
		assertTrue(CollectionUtils.isEmpty(complex.getAttributes()));
		assertEquals(2, CollectionUtils.sizeOf(complex.getElements()));
		assertSame(element1, complex.getElements().get(0));
		assertSame(element2, complex.getElements().get(1));
	}

	/**
	 * Tests creating a new complex serialization
	 */
	public void testCreateComplex() {
		Serialization attr1 = SerializationUtils.createAttribute("attr1");
		Serialization attr2 = SerializationUtils.createAttribute("attr2");
		Serialization element1 = SerializationUtils.createValue("element1");
		Serialization element2 = SerializationUtils.createValue("element2");
		NameSerialization name = SerializationUtils.createComplex("name", CollectionUtils.asList(attr1, attr2), CollectionUtils.asList(element1, element2));
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		ComplexSerialization complex = (ComplexSerialization) delegate;
		assertEquals(2, CollectionUtils.sizeOf(complex.getAttributes()));
		assertSame(attr1, complex.getAttributes().get(0));
		assertSame(attr2, complex.getAttributes().get(1));
		assertEquals(2, CollectionUtils.sizeOf(complex.getElements()));
		assertSame(element1, complex.getElements().get(0));
		assertSame(element2, complex.getElements().get(1));
	}

	/**
	 * Tests creating a new reference serialization
	 */
	public void testCreateReference() {
		Serialization referenced = SerializationUtils.createComplexWithElements("name");
		ReferenceSerialization reference = SerializationUtils.createReference(referenced);
		assertSame(referenced, reference.getReferenced());
		assertEquals(referenced.isWriteEmpty(), reference.isWriteEmpty());
	}
}
