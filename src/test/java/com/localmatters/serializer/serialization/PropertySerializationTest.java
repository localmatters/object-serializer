package com.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.InvalidPropertyException;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.writer.Writer;

/**
 * Tests the <code>PropertySerialization</code>
 */
public class PropertySerializationTest extends TestCase {
	private PropertySerialization ser;
	private Serialization parentSer;
	private Serialization delegate;
	private PropertyResolver resolver;
	private Writer writer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		parentSer = createMock(Serialization.class);
		resolver = createMock(PropertyResolver.class);
		ser = new PropertySerialization();
		ser.setDelegate(delegate);
		writer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(writer, resolver, null);
	}

	/**
	 * Tests the serialization when the object is null and not write empty
	 */
	public void testHandleNullObjectNotWriteEmpty() throws Exception {
		ser.setProperty("name");
		expect(delegate.isWriteEmpty()).andReturn(false);
		replay(delegate, resolver, writer, parentSer);
		ser.serialize(parentSer, null, null, ctx);
		verify(delegate, resolver, writer, parentSer);
	}

	/**
	 * Tests the serialization when the object is null and write empty
	 */
	public void testHandleNullObjectWriteEmpty() throws Exception {
		ser.setProperty("name");
		expect(delegate.isWriteEmpty()).andReturn(true);
		delegate.serialize(parentSer, "name", null, ctx);
		replay(delegate, resolver, writer, parentSer);
		ser.serialize(parentSer, null, null, ctx);
		verify(delegate, resolver, writer, parentSer);
	}

	/**
	 * Tests the serialization when the property is invalid
	 */
	public void testHandleInvalidProperty() throws Exception {
		ser.setProperty("name");
		expect(resolver.resolve(object, "name")).andThrow(new InvalidPropertyException("name", "Object"));
		replay(delegate, resolver, writer, parentSer);
		try {
			ser.serialize(parentSer, null, object, ctx);
			fail("UnknownPropertyException expected");
		} catch (UnknownPropertyException e) {
		}
		verify(delegate, resolver, writer, parentSer);
	}
	
	/**
	 * Tests the serialization when the name is null
	 */
	public void testHandleWhenTheNameIsNull() throws Exception {
		ser.setProperty("name");
		expect(resolver.resolve(object, "name")).andReturn("John Doe");
		delegate.serialize(parentSer, "name", "John Doe", ctx);
		replay(delegate, resolver, writer, parentSer);
		ser.serialize(parentSer, null, object, ctx);
		verify(delegate, resolver, writer, parentSer);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		ser.setProperty("name");
		expect(resolver.resolve(object, "name")).andReturn("John Doe");
		delegate.serialize(parentSer, "firstlastname", "John Doe", ctx);
		replay(delegate, resolver, writer, parentSer);
		ser.serialize(parentSer, "firstlastname", object, ctx);
		verify(delegate, resolver, writer, parentSer);
	}
}
