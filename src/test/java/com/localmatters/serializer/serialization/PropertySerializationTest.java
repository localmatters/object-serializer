package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.InvalidPropertyException;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.UnknownPropertyException;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>PropertySerialization</code>
 */
public class PropertySerializationTest extends TestCase {
	private PropertySerialization serialization;
	private Serialization delegate;
	private PropertyResolver resolver;
	private Writer serializer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		resolver = createMock(PropertyResolver.class);
		serialization = new PropertySerialization();
		serialization.setDelegate(delegate);
		serializer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), resolver);
	}

	/**
	 * Tests the serialization when the property is invalid
	 */
	public void testHandleInvalidProperty() throws Exception {
		serialization.setProperty("name");
		expect(resolver.resolve(object, "name")).andThrow(new InvalidPropertyException("name", "Object"));
		replay(delegate, resolver, serializer);
		try {
			serialization.serialize(object, ctx);
			fail("UnknownPropertyException expected");
		} catch (UnknownPropertyException e) {
		}
		verify(delegate, resolver, serializer);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		serialization.setProperty("name");
		expect(resolver.resolve(object, "name")).andReturn("John Doe");
		expect(delegate.serialize("John Doe", ctx)).andReturn("<name>John Doe</name>");
		replay(delegate, resolver, serializer);
		String result = serialization.serialize(object, ctx);
		verify(delegate, resolver, serializer);
		assertEquals("<name>John Doe</name>", result);
	}
}
