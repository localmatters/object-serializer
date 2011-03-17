package org.localmatters.serializer.resolver;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.localmatters.serializer.resolver.ComplexPropertyResolver;
import org.localmatters.serializer.resolver.InvalidPropertyException;
import org.localmatters.serializer.resolver.PropertyResolver;
import org.localmatters.serializer.resolver.PropertyResolverException;

import junit.framework.TestCase;

/**
 * Tests the <code>ComplexPropertyResolver</code>
 */
public class ComplexPropertyResolverTest extends TestCase {
	private ComplexPropertyResolver resolver;
	private PropertyResolver delegate;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(PropertyResolver.class);
		resolver = new ComplexPropertyResolver();
		resolver.setDelegate(delegate);
		resolver.setToken(".");
	}

	/**
	 * Tests the resolution when the property is blank
	 */
	public void testBlank() {
		Object bean = new Object();
		replay(delegate);
		try {
			resolver.resolve(bean, "   ");
			fail("InvalidPropertyException expected");
		} catch (InvalidPropertyException e) {
		} catch (PropertyResolverException e) {
			fail("InvalidPropertyException expected");
		}
		verify(delegate);
	}

	/**
	 * Tests the resolution when the property has an empty token
	 */
	public void testEmptyToken() throws Exception {
		Object bean = new Object();
		Object property = new Object();
		expect(delegate.resolve(bean, "listing")).andReturn(property);
		replay(delegate);
		try {
			resolver.resolve(bean, "listing..name");
			fail("InvalidPropertyException expected");
		} catch (InvalidPropertyException e) {
		}
		verify(delegate);
	}

	/**
	 * Tests the resolution when the property is already simple
	 */
	public void testSimple() throws Exception {
		Object bean = new Object();
		Object property = new Object();
		expect(delegate.resolve(bean, "name")).andReturn(property);
		replay(delegate);
		assertSame(property, resolver.resolve(bean, "name"));
		verify(delegate);
	}

	/**
	 * Tests the resolution when one of the property level is null
	 */
	public void testIntermediateNull() throws Exception {
		Object bean = new Object();
		Object property = new Object();
		expect(delegate.resolve(bean, "listing")).andReturn(property);
		expect(delegate.resolve(property, "addresses{home}")).andReturn(null);
		replay(delegate);
		assertNull(resolver.resolve(bean, "listing.addresses{home}.street"));
		verify(delegate);
	}

	/**
	 * Tests the resolution when the property is complex
	 */
	public void testComplex() throws Exception {
		Object bean = new Object();
		Object results = new Object();
		Object listing = new Object();
		Object address = new Object();
		Object street = new Object();
		expect(delegate.resolve(bean, "results")).andReturn(results);
		expect(delegate.resolve(results, "listings[23]")).andReturn(listing);
		expect(delegate.resolve(listing, "addresses{home}")).andReturn(address);
		expect(delegate.resolve(address, "street")).andReturn(street);
		replay(delegate);
		assertSame(street, resolver.resolve(bean, "results.listings[23].addresses{home}.street"));
		verify(delegate);
	}
}
