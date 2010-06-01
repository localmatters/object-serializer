/**
 * 
 */
package com.localmatters.serializer.util;

import junit.framework.TestCase;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
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
	 * Tests creating a new value serialization
	 */
	public void testCreateValue() {
		NameSerialization name = SerializationUtils.createValue("name");
		assertEquals("name", name.getName());
		assertNotNull(name.getDelegate());
		assertTrue(name.getDelegate() instanceof ValueSerialization);
	}
	
	/**
	 * Test getting the singular name
	 */
	public void testGetSingular() {
		assertEquals("default", SerializationUtils.getSingular("value", "default"));
		assertEquals("value", SerializationUtils.getSingular("values", "default"));
		assertEquals("value", SerializationUtils.getSingular("valueList", "default"));
		assertEquals("value", SerializationUtils.getSingular("valuesList", "default"));
		assertEquals("value", SerializationUtils.getSingular("valueMap", "default"));
		assertEquals("value", SerializationUtils.getSingular("valuesMap", "default"));
		assertEquals("default", SerializationUtils.getSingular("valueEntry", "default"));
		assertEquals("valueEntry", SerializationUtils.getSingular("valuesEntry", "default"));

		assertEquals("default", SerializationUtils.getSingular("address", "default"));
		assertEquals("default", SerializationUtils.getSingular("As", "default"));
		assertEquals("address", SerializationUtils.getSingular("addresses", "default"));
		assertEquals("address", SerializationUtils.getSingular("addressList", "default"));
		assertEquals("address", SerializationUtils.getSingular("addressesList", "default"));
		assertEquals("address", SerializationUtils.getSingular("addressMap", "default"));
		assertEquals("address", SerializationUtils.getSingular("addressesMap", "default"));
		assertEquals("default", SerializationUtils.getSingular("addressEntry", "default"));
		assertEquals("addressEntry", SerializationUtils.getSingular("addressesEntry", "default"));
		assertEquals("addressesEntry", SerializationUtils.getSingular("addressesEntries", "default"));
		
		assertEquals("default", SerializationUtils.getSingular("company", "default"));
		assertEquals("company", SerializationUtils.getSingular("companies", "default"));
		assertEquals("company", SerializationUtils.getSingular("companyList", "default"));
		assertEquals("company", SerializationUtils.getSingular("companiesList", "default"));
		assertEquals("company", SerializationUtils.getSingular("companyMap", "default"));
		assertEquals("company", SerializationUtils.getSingular("companiesMap", "default"));
		assertEquals("default", SerializationUtils.getSingular("companyEntry", "default"));
		assertEquals("companyEntry", SerializationUtils.getSingular("companiesEntry", "default"));
		
		assertEquals("company", SerializationUtils.getSingular("companiesArray", "default"));
		assertEquals("company", SerializationUtils.getSingular("companiesIndex", "default"));
		assertEquals("company", SerializationUtils.getSingular("companiesSet", "default"));
		assertEquals("company", SerializationUtils.getSingular("companiesCollection", "default"));

		assertEquals("y", SerializationUtils.getSingular("ies", "default"));
	}
}
