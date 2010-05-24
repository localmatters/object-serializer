package com.localmatters.serializer.resolver;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.localmatters.util.CollectionUtils;

import junit.framework.TestCase;

/**
 * Tests the <code>BeanUtilsPropertyResolver</code>
 */
public class BeanUtilsPropertyResolverTest extends TestCase {
	private BeanUtilsPropertyResolver resolver;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		resolver = new BeanUtilsPropertyResolver();
		resolver.setIndexedMappedRemoverPattern(Pattern.compile("^([^\\[\\{]+)(?:\\[|\\{).*$"));
	}

	/**
	 * Tests resolving an invalid property
	 */
	public void testInvalidProperty() {
		try {
			resolver.resolve(new Dummy(), "type");
			fail("InvalidPropertyException expected");
		} catch (InvalidPropertyException e) {
		}
	}

	/**
	 * Tests resolving a valid property
	 */
	public void testValidProperty() {
		try {
			assertEquals("12345", resolver.resolve(new Dummy(), "id"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a null list property
	 */
	public void testNullListProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "photos[0]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a non-null list property
	 */
	public void testListProperty() {
		try {
			assertEquals("we are fun", resolver.resolve(new Dummy(), "ads[1]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a null map property
	 */
	public void testNullMapProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "addresses{home}"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}
	
	
	/**
	 * Class describing a dummy object for testing
	 */
	public class Dummy {
		public String getId() {return "12345";}
		public String getName() {return "dummy";}
		public List<String> getPhotos() {return null;}
		public Map<String, String> getAddresses() {return null;}
		public List<String> getAds() {return CollectionUtils.asList("buy from us", "we are fun");}
	}
}
