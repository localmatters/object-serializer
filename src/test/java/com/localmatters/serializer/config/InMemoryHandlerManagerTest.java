package com.localmatters.serializer.config;

import junit.framework.TestCase;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import com.localmatters.serializer.config.ConfigurationException;
import com.localmatters.serializer.config.InMemorySerializationManager;
import com.localmatters.serializer.config.SerializationElementHandler;
import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
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
		assertEquals("listing", listing.getName());
		assertTrue(listing.isWriteEmpty());
		assertTrue(listing instanceof BeanSerialization);
		
		// listing bean
		BeanSerialization bean = (BeanSerialization) listing;
		Serialization delegate = bean.getDelegate();
		assertNotNull(delegate);
		assertEquals("listing", delegate.getName());
		assertTrue(delegate.isWriteEmpty());
		assertTrue(delegate instanceof PropertySerialization);
		
		// listing property
		PropertySerialization property = (PropertySerialization) delegate;
		assertEquals("listings[0]", property.getProperty());
		delegate = property.getDelegate();
		assertNotNull(delegate);
		assertEquals("listing", delegate.getName());
		assertTrue(delegate.isWriteEmpty());
		assertTrue(delegate instanceof ComplexSerialization);
		
		// complex
		ComplexSerialization complex = (ComplexSerialization) delegate;
		assertEquals(1, CollectionUtils.sizeOf(complex.getAttributes()));
		assertEquals(1, CollectionUtils.sizeOf(complex.getElements()));
		assertTrue(complex.getAttributes().get(0) instanceof PropertySerialization);
		assertTrue(complex.getElements().get(0) instanceof ReferenceSerialization);
		
		// attribute property
		property = (PropertySerialization) complex.getAttributes().iterator().next();
		assertEquals("businessName", property.getProperty());
		delegate = property.getDelegate();
		assertNotNull(delegate);
		assertEquals("name", delegate.getName());
		assertFalse(delegate.isWriteEmpty());
		assertTrue(delegate instanceof AttributeSerialization);
		
		// address reference
		ReferenceSerialization reference = (ReferenceSerialization) complex.getElements().iterator().next();
		assertNotNull(reference);
		assertEquals("listingAddress", reference.getName());
		assertTrue(reference.isWriteEmpty());

		Serialization address = manager.getSerialization("addressConfig");
		assertNotNull(address);
		assertSame(address, reference.getReferenced());
	}
}
