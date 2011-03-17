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
package org.localmatters.serializer.util;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.localmatters.serializer.serialization.AttributeSerialization;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.ConstantSerialization;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;


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
		assertEquals(2, CollectionUtils.size(complex.getAttributes()));
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
		assertEquals(2, CollectionUtils.size(complex.getAttributes()));
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
