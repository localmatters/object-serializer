package com.localmatters.serializer.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;
import com.localmatters.serializer.resolver.InvalidPropertyException;
import com.localmatters.serializer.resolver.PropertyResolver;

/**
 * Tests the <code>PropertyConfig</code>
 */
public class PropertyConfigTest extends TestCase {
	private PropertyConfig config;
	private Config delegate;
	private PropertyResolver resolver;
	private Serializer serializer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Config.class); 
		resolver = createMock(PropertyResolver.class);
		config = new PropertyConfig();
		config.setDelegate(delegate);
		serializer = createMock(Serializer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), resolver);
	}

	/**
	 * Tests handling when the property is invalid
	 */
	public void testHandleInvalidProperty() throws Exception {
		config.setProperty("name");
		expect(resolver.resolve(object, "name")).andThrow(new InvalidPropertyException("name", "Object"));
		replay(delegate, resolver, serializer);
		try {
			config.handle(object, ctx);
			fail("UnknownPropertyException expected");
		} catch (UnknownPropertyException e) {
		}
		verify(delegate, resolver, serializer);
	}
	
	/**
	 * Tests handling when the property is valid
	 */
	public void testHandle() throws Exception {
		config.setProperty("name");
		expect(resolver.resolve(object, "name")).andReturn("John Doe");
		expect(delegate.handle("John Doe", ctx)).andReturn("<name>John Doe</name>");
		replay(delegate, resolver, serializer);
		String result = config.handle(object, ctx);
		verify(delegate, resolver, serializer);
		assertEquals("<name>John Doe</name>", result);
	}
}
