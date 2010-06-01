/**
 * 
 */
package com.localmatters.serializer.util;

import junit.framework.TestCase;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
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
	 * Tests creating a new constant value serialization
	 */
	public void testCreateConstantValue() {
		NameSerialization name = SerializationUtils.createConstantAttribute("name", "constant");
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
	public void testCreateComplex() {
		Serialization attr1 = new AttributeSerialization();
		Serialization attr2 = new AttributeSerialization();
		ComplexSerialization complex = SerializationUtils.createComplex(attr1, attr2);
		assertEquals(2, CollectionUtils.sizeOf(complex.getAttributes()));
		assertSame(attr1, complex.getAttributes().get(0));
		assertSame(attr2, complex.getAttributes().get(1));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
	} 
	
	/**
	 * Tests creating a new complex named serialization with attributes
	 */
	public void testCreateNamedComplex() {
		Serialization attr1 = new AttributeSerialization();
		Serialization attr2 = new AttributeSerialization();
		NameSerialization name = SerializationUtils.createComplex("name", attr1, attr2);
		assertEquals("name", name.getName());
		Serialization delegate = name.getDelegate();
		ComplexSerialization complex = (ComplexSerialization) delegate;
		assertEquals(2, CollectionUtils.sizeOf(complex.getAttributes()));
		assertSame(attr1, complex.getAttributes().get(0));
		assertSame(attr2, complex.getAttributes().get(1));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
	}

	/**
	 * Tests creating a new name serialization
	 */
	public void testCreateName() {
		Serialization delegate = SerializationUtils.createComplex();
		NameSerialization name = SerializationUtils.createName("name", delegate);
		assertSame(delegate, name.getDelegate());
		assertEquals("name", name.getName());
	}

	/**
	 * Tests creating a new reference serialization
	 */
	public void testCreateReference() {
		Serialization referenced = SerializationUtils.createComplex();
		ReferenceSerialization reference = SerializationUtils.createReference(referenced);
		assertSame(referenced, reference.getReferenced());
		assertEquals(referenced.isWriteEmpty(), reference.isWriteEmpty());
	}

	/**
	 * Tests creating a new value serialization
	 */
	public void testCreateValue() {
		NameSerialization name = SerializationUtils.createValue("name");
		assertEquals("name", name.getName());
		assertNotNull(name.getDelegate());
		assertTrue(name.getDelegate() instanceof ValueSerialization);
	}
}
