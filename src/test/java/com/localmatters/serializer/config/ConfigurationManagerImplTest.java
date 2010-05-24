package com.localmatters.serializer.config;

import junit.framework.TestCase;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import com.localmatters.serializer.config.AttributeConfig;
import com.localmatters.serializer.config.BeanConfig;
import com.localmatters.serializer.config.ComplexConfig;
import com.localmatters.serializer.config.Config;
import com.localmatters.serializer.config.ConfigurationException;
import com.localmatters.serializer.config.ConfigurationHandler;
import com.localmatters.serializer.config.ConfigurationManagerImpl;
import com.localmatters.serializer.config.PropertyConfig;
import com.localmatters.serializer.config.ReferenceConfig;
import com.localmatters.serializer.util.TestObjectFactory;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.refreshable.InitialRefreshFailedException;

/**
 * Tests the <code>ConfigurationManagerImpl</code>
 */
public class ConfigurationManagerImplTest extends TestCase {
	private ConfigurationManagerImpl manager;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		manager = new ConfigurationManagerImpl();
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
			assertEquals("Unable to load the configuration aFile.xml!", e.getCause().getMessage());
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
			assertEquals(String.format(ConfigurationHandler.INVALID_ID_FORMAT, CollectionUtils.asList("address", "payments")), e.getCause().getMessage());
		}
	}

	/**
	 * Tests loading a valid file
	 */
	public void testRefresh() {
		manager.setResource(new ClassPathResource("test-config.xml"));
		manager.refresh();

		Config listing = manager.getConfig("listingConfig");
		assertNotNull(listing);
		assertEquals("listing", listing.getName());
		assertTrue(listing.isWriteEmpty());
		assertTrue(listing instanceof BeanConfig);
		
		// listing bean
		BeanConfig bean = (BeanConfig) listing;
		Config delegate = bean.getDelegate();
		assertNotNull(delegate);
		assertEquals("listing", delegate.getName());
		assertTrue(delegate.isWriteEmpty());
		assertTrue(delegate instanceof PropertyConfig);
		
		// listing property
		PropertyConfig property = (PropertyConfig) delegate;
		assertEquals("listings[0]", property.getProperty());
		delegate = property.getDelegate();
		assertNotNull(delegate);
		assertEquals("listing", delegate.getName());
		assertTrue(delegate.isWriteEmpty());
		assertTrue(delegate instanceof ComplexConfig);
		
		// complex
		ComplexConfig complex = (ComplexConfig) delegate;
		assertEquals(1, CollectionUtils.sizeOf(complex.getAttributeConfigs()));
		assertEquals(1, CollectionUtils.sizeOf(complex.getElementConfigs()));
		assertTrue(complex.getAttributeConfigs().iterator().next() instanceof PropertyConfig);
		assertTrue(complex.getElementConfigs().iterator().next() instanceof ReferenceConfig);
		
		// attribute property
		property = (PropertyConfig) complex.getAttributeConfigs().iterator().next();
		assertEquals("businessName", property.getProperty());
		delegate = property.getDelegate();
		assertNotNull(delegate);
		assertEquals("name", delegate.getName());
		assertFalse(delegate.isWriteEmpty());
		assertTrue(delegate instanceof AttributeConfig);
		
		// address reference
		ReferenceConfig reference = (ReferenceConfig) complex.getElementConfigs().iterator().next();
		assertNotNull(reference);
		assertEquals("listingAddress", reference.getName());
		assertTrue(reference.isWriteEmpty());

		Config address = manager.getConfig("addressConfig");
		assertNotNull(address);
		assertSame(address, reference.getDelegate());
	}
}
