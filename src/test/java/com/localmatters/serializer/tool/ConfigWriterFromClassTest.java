package com.localmatters.serializer.tool;

import com.localmatters.serializer.test.DummyObject;

import junit.framework.TestCase;

/**
 * Tests the <code>ConfigWriterFromClass</code>
 */
public class ConfigWriterFromClassTest extends TestCase {
	private ConfigWriterFromClass writer;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		writer = new ConfigWriterFromClass(DummyObject.class);
	}
	
	/**
	 * Test getting the singular name
	 */
	public void testGetSingular() {

		assertEquals("default", writer.getSingular("value", "default"));
		assertEquals("value", writer.getSingular("values", "default"));
		assertEquals("value", writer.getSingular("valueList", "default"));
		assertEquals("value", writer.getSingular("valuesList", "default"));
		assertEquals("value", writer.getSingular("valueMap", "default"));
		assertEquals("value", writer.getSingular("valuesMap", "default"));
		assertEquals("default", writer.getSingular("valueEntry", "default"));
		assertEquals("valueEntry", writer.getSingular("valuesEntry", "default"));

		assertEquals("default", writer.getSingular("address", "default"));
		assertEquals("address", writer.getSingular("addresses", "default"));
		assertEquals("address", writer.getSingular("addressList", "default"));
		assertEquals("address", writer.getSingular("addressesList", "default"));
		assertEquals("address", writer.getSingular("addressMap", "default"));
		assertEquals("address", writer.getSingular("addressesMap", "default"));
		assertEquals("default", writer.getSingular("addressEntry", "default"));
		assertEquals("addressEntry", writer.getSingular("addressesEntry", "default"));
		assertEquals("addressesEntry", writer.getSingular("addressesEntries", "default"));
		
		assertEquals("default", writer.getSingular("company", "default"));
		assertEquals("company", writer.getSingular("companies", "default"));
		assertEquals("company", writer.getSingular("companyList", "default"));
		assertEquals("company", writer.getSingular("companiesList", "default"));
		assertEquals("company", writer.getSingular("companyMap", "default"));
		assertEquals("company", writer.getSingular("companiesMap", "default"));
		assertEquals("default", writer.getSingular("companyEntry", "default"));
		assertEquals("companyEntry", writer.getSingular("companiesEntry", "default"));
		
		assertEquals("company", writer.getSingular("companiesArray", "default"));
		assertEquals("company", writer.getSingular("companiesIndex", "default"));
		assertEquals("company", writer.getSingular("companiesSet", "default"));
		assertEquals("company", writer.getSingular("companiesCollection", "default"));
	}

}
