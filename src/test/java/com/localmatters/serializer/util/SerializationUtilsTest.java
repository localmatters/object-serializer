/**
 * 
 */
package com.localmatters.serializer.util;

import junit.framework.TestCase;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
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
	 * Tests creating a new attribute serialization
	 */
	public void testCreateAttribute() {
		AttributeSerialization attribute = SerializationUtils.createAttribute("name");
		assertEquals("name", attribute.getName());
	}

	/**
	 * Tests creating a new property attribute serialization
	 */
	public void testCreatePropertyAttribute() {
		PropertySerialization property = SerializationUtils.createPropertyAttribute("name", "property");
		assertEquals("name", property.getName());
		assertEquals("property", property.getProperty());
		Serialization attribute = property.getDelegate();
		assertTrue(attribute instanceof AttributeSerialization);
		assertEquals("name", attribute.getName());
	}

	/**
	 * Tests creating a new constant attribute serialization
	 */
	public void testCreateConstantAttribute() {
		ConstantSerialization constant = SerializationUtils.createConstantAttribute("name", "constant");
		assertEquals("name", constant.getName());
		assertEquals("constant", constant.getConstant());
		Serialization attribute = constant.getDelegate();
		assertTrue(attribute instanceof AttributeSerialization);
		assertEquals("name", attribute.getName());
	}

	/**
	 * Tests creating a new complex serialization
	 */
	public void testCreateComplex() {
		ComplexSerialization complex = SerializationUtils.createComplex("name");
		assertEquals("name", complex.getName());
		assertTrue(CollectionUtils.isEmpty(complex.getAttributes()));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
	}

	/**
	 * Tests creating a new complex serialization with attribute
	 */
	public void testCreateComplexWithAttributes() {
		Serialization attr1 = SerializationUtils.createAttribute("attr1");
		Serialization attr2 = SerializationUtils.createAttribute("attr2");
		ComplexSerialization complex = SerializationUtils.createComplex("name", attr1, attr2);
		assertEquals("name", complex.getName());
		assertEquals(2, CollectionUtils.sizeOf(complex.getAttributes()));
		assertSame(attr1, complex.getAttributes().get(0));
		assertSame(attr2, complex.getAttributes().get(1));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
	}

	/**
	 * Tests creating a new reference serialization
	 */
	public void testCreateReference() {
		Serialization referenced = SerializationUtils.createComplex("name");
		ReferenceSerialization reference = SerializationUtils.createReference(referenced);
		assertSame(referenced, reference.getReferenced());
		assertEquals(referenced.getName(), reference.getName());
		assertEquals(referenced.isWriteEmpty(), reference.isWriteEmpty());
	}
	
	
}
