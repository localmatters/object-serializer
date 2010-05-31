package com.localmatters.serializer.config;

import junit.framework.TestCase;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.test.TestObjectFactory;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.refreshable.InitialRefreshFailedException;

/**
 * Tests the <code>InMemoryHandlerManager</code>
 */
public class InMemoryHandlerManagerTest extends TestCase {
	private InMemorySerializationManager manager;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		manager = new InMemorySerializationManager();
		manager.setObjectFactory(new TestObjectFactory());
		manager.setForwardExceptionIfInitialLoadFailed(true);
	}

	/**
	 * Tests loading an invalid file
	 */
	public void testUnknownFile() {
		manager.setEncoding("UTF-8");
		manager.setResource(new ClassPathResource("AnAMEthaTSH0uldNotBevaliD"));
		try {
			manager.refresh();
			fail("InitialRefreshFailedException expected");
		} catch (InitialRefreshFailedException e) {
			assertNotNull(e.getCause());
			assertFalse(e.getCause() instanceof ConfigurationException);
		}
	}

	/**
	 * Tests loading an invalid file
	 */
	public void testInvalidFile() {
		manager.setResource(new ByteArrayResource("".getBytes(), "aFile.xml"));
		try {
			manager.refresh();
			fail("InitialRefreshFailedException expected");
		} catch (InitialRefreshFailedException e) {
			assertNotNull(e.getCause());
			assertEquals("Unable to load the handlers configuration aFile.xml!", e.getCause().getMessage());
		}
	}

	/**
	 * Tests loading a file with invalid references
	 */
	public void testInvalidRefs() {
		manager.setResource(new ClassPathResource("test-invalid-refs.xml"));
		try {
			manager.refresh();
			fail("InitialRefreshFailedException expected");
		} catch (InitialRefreshFailedException e) {
			assertNotNull(e.getCause());
			assertEquals(String.format(SerializationElementHandler.INVALID_ID_FORMAT, CollectionUtils.asList("address", "payments")), e.getCause().getMessage());
		}
	}

	/**
	 * Tests loading a valid file
	 */
	public void testRefresh() {
		manager.setResource(new ClassPathResource("test-config.xml"));
		manager.refresh();

		Serialization listing = manager.getSerialization("listingConfig");
		assertNotNull(listing);
		assertTrue(listing instanceof NameSerialization);
		
		// listing name
		NameSerialization name = (NameSerialization) listing;
		assertEquals("listing", name.getName());
		assertFalse(name.isWriteEmpty());
		assertTrue(name.getDelegate() instanceof BeanSerialization);
		
		// listing bean
		BeanSerialization bean = (BeanSerialization) name.getDelegate();
		assertEquals("results", bean.getBean());
		assertFalse(bean.isWriteEmpty());
		assertTrue(bean.getDelegate() instanceof PropertySerialization);
		
		// listing property
		PropertySerialization property = (PropertySerialization) bean.getDelegate();
		assertEquals("listings[0]", property.getProperty());
		assertFalse(property.isWriteEmpty());
		assertTrue(property.getDelegate() instanceof ComplexSerialization);
		
		// complex
		ComplexSerialization complex = (ComplexSerialization) property.getDelegate();
		assertEquals(1, CollectionUtils.sizeOf(complex.getAttributes()));
		assertEquals(1, CollectionUtils.sizeOf(complex.getElements()));
		assertTrue(CollectionUtils.isEmpty(complex.getComments()));
		assertTrue(complex.getAttributes().get(0) instanceof NameSerialization);
		assertTrue(complex.getElements().get(0) instanceof NameSerialization);
		
		// attribute name
		name = (NameSerialization) complex.getAttributes().get(0);
		assertEquals("name", name.getName());
		assertFalse(name.isWriteEmpty());
		assertTrue(name.getDelegate() instanceof PropertySerialization);
		
		// attribute property
		property = (PropertySerialization) name.getDelegate();
		assertEquals("businessName", property.getProperty());
		assertFalse(property.isWriteEmpty());
		assertTrue(property.getDelegate() instanceof AttributeSerialization);

		// element name
		name = (NameSerialization) complex.getElements().get(0);
		assertEquals("listingAddress", name.getName());
		assertFalse(name.isWriteEmpty());
		assertTrue(name.getDelegate() instanceof ReferenceSerialization);

		// element reference
		ReferenceSerialization reference = (ReferenceSerialization) name.getDelegate();
		assertFalse(reference.isWriteEmpty());

		Serialization address = manager.getSerialization("addressConfig");
		assertNotNull(address);
		assertSame(address, reference.getReferenced());
	}
}
