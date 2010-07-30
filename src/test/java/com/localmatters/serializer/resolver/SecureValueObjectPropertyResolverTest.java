package com.localmatters.serializer.resolver;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.replay;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import com.localmatters.mvc.transfer.ValueObject;
import com.localmatters.util.CollectionUtils;

/**
 * Tests the <code>SecureValueObjectPropertyResolver</code>
 */
public class SecureValueObjectPropertyResolverTest extends TestCase {
	private SecureValueObjectPropertyResolver resolver;
	private PropertyResolver delegate;
	private Map<String, Collection<String>> publicProperties;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(PropertyResolver.class);
		publicProperties = new HashMap<String, Collection<String>>();
		publicProperties.put("dummy2", CollectionUtils.asList("name", "addresses"));
		resolver = new SecureValueObjectPropertyResolver();
		resolver.setDelegate(delegate);
		resolver.setIndexedMappedRemoverPattern(Pattern.compile("^([^\\[\\{]+)(?:\\[|\\{).*$"));
		resolver.setPublicProperties(publicProperties);
	}
	
	/**
	 * Tests resolving a non value object
	 */
	public void testNonValueObject() throws Exception {
		Object bean = new Object();
		Object property = new Object();
		expect(delegate.resolve(bean, "name")).andReturn(property);
		replay(delegate);
		assertSame(property, resolver.resolve(bean, "name"));
		verify(delegate);
	}
	
	/**
	 * Tests resolving a private value object
	 */
	public void testPrivateValueObject() {
		DummyVO1 bean = new DummyVO1();
		
		replay(delegate);
		try {
			resolver.resolve(bean, "orders");
			fail("NotPublicPropertyException expected");
		} catch (NotPublicPropertyException e) {
			assertTrue(e.getMessage().contains("orders"));
		} catch (PropertyResolverException e) {
			fail("NotPublicPropertyException expected");
		}
		verify(delegate);
	}
	
	/**
	 * Tests resolving a private value object's property
	 */
	public void testPrivateProperty() {
		DummyVO2 bean = new DummyVO2();
		replay(delegate);
		try {
			resolver.resolve(bean, "orders[0]");
			fail("NotPublicPropertyException expected");
		} catch (NotPublicPropertyException e) {
			assertFalse(e.getMessage().contains("orders[0]"));
			assertTrue(e.getMessage().contains("orders"));
		} catch (PropertyResolverException e) {
			fail("NotPublicPropertyException expected");
		}
		verify(delegate);
	}
	
	/**
	 * Tests resolving a public value object's property
	 */
	public void testPublicProperty() throws Exception {
		DummyVO2 bean = new DummyVO2();
		Object property = new Object();
		expect(delegate.resolve(bean, "addresses{home}")).andReturn(property);
		replay(delegate);
		assertSame(property, resolver.resolve(bean, "addresses{home}"));
		verify(delegate);
	}

	/**
	 * Dummy implementation of a first value object
	 */
	private class DummyVO1 implements ValueObject {
		private static final long serialVersionUID = 1L;
		public String getAttributeKey() {return "dummy1";}
		@SuppressWarnings("rawtypes")
		public Map getHiddenFormParameters() {return null;}
	}

	/**
	 * Dummy implementation of a second value object
	 */
	private class DummyVO2 implements ValueObject {
		private static final long serialVersionUID = 1L;
		public String getAttributeKey() {return "dummy2";}
		@SuppressWarnings("rawtypes")
		public Map getHiddenFormParameters() {return null;}
	}
}
