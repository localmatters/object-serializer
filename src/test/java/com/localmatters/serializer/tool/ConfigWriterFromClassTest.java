package com.localmatters.serializer.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.test.domain.ChildOfSelfreferencingObject;
import com.localmatters.serializer.test.domain.DummyObject;
import com.localmatters.serializer.test.domain.ObjectWithGenerics;

/**
 * Tests the <code>ConfigWriterFromClass</code>
 */
public class ConfigWriterFromClassTest extends TestCase {

	/**
	 * Tests getting the configuration for the <code>ObjectWithGenerics</code>
	 * class
	 */
	public void testConfigForObjectWithGenerics() throws Exception {
		Resource resource = new ClassPathResource("test-object-with-generics-config.xml");
		assertEquals(resourceToString(resource), ConfigWriterFromClass.getConfiguration(ObjectWithGenerics.class.getName()));
	}

	/**
	 * Tests getting the configuration for the <code>DummyObject</code>
	 * class
	 */
	public void testConfigForDummyObject() throws Exception {
		Resource resource = new ClassPathResource("test-dummy-object-config.xml");
		assertEquals(resourceToString(resource), ConfigWriterFromClass.getConfiguration(DummyObject.class));
	}

	/**
	 * Tests getting the configuration for multiple classes
	 */
	public void testConfigForObjectWithGenericsDummyObjectAndString() throws Exception {
		Resource resource = new ClassPathResource("test-multiple-config.xml");
		assertEquals(resourceToString(resource), ConfigWriterFromClass.getConfiguration(ObjectWithGenerics.class, DummyObject.class, com.localmatters.serializer.test.domain.extra.DummyObject.class, ChildOfSelfreferencingObject.class));
	}
	
	/**
	 * Tests handling the iterator when the type array is invalid
	 */
	public void testHandleIteratorWithInvalidType() {
		ConfigWriterFromClass writer = new ConfigWriterFromClass(Object.class);
		AttributeSerialization attribute = new AttributeSerialization();
		NameSerialization name = writer.handleIterator("addresses", List.class, new Type[]{}, attribute);
		assertEquals("list", name.getName());
		assertTrue(name.getDelegate() instanceof ComplexSerialization);
		ComplexSerialization list = (ComplexSerialization) name.getDelegate();
		assertEquals(1, CollectionUtils.size(list.getAttributes()));
		assertSame(attribute, list.getAttributes().get(0));
		assertEquals(1, CollectionUtils.size(list.getElements()));
		assertEquals(2, CollectionUtils.size(list.getComments()));
		assertEquals("Unable to identify the types for the [addresses] iteration!", list.getComments().get(0));
		assertEquals("The configuration of its elements must be written manually.", list.getComments().get(1));
	}
	
	/**
	 * Tests handling the map when the type array is invalid
	 */
	public void testHandleMapWithInvalidType() {
		ConfigWriterFromClass writer = new ConfigWriterFromClass(Object.class);
		AttributeSerialization attribute = new AttributeSerialization();
		NameSerialization name = writer.handleMap("addresses", Map.class, new Type[]{}, attribute);
		assertEquals("map", name.getName());
		assertTrue(name.getDelegate() instanceof ComplexSerialization);
		ComplexSerialization list = (ComplexSerialization) name.getDelegate();
		assertEquals(1, CollectionUtils.size(list.getAttributes()));
		assertSame(attribute, list.getAttributes().get(0));
		assertTrue(CollectionUtils.isEmpty(list.getElements()));
		assertEquals(2, CollectionUtils.size(list.getComments()));
		assertEquals("Unable to identify the types for the [addresses] map!", list.getComments().get(0));
		assertEquals("The configuration of its entries must be written manually.", list.getComments().get(1));
	}
	
	/**
	 * Tests handling the map when the second entry in the type array is not a
	 * class and the first one is a simple
	 */
	public void testHandleMapWithNonClassSecondType() {
		ConfigWriterFromClass writer = new ConfigWriterFromClass(Object.class);
		AttributeSerialization attribute = new AttributeSerialization();
		NameSerialization name = writer.handleMap("addresses", Map.class, new Type[]{String.class, new DummyType()}, attribute);
		assertEquals("map", name.getName());
		assertTrue(name.getDelegate() instanceof ComplexSerialization);
		ComplexSerialization list = (ComplexSerialization) name.getDelegate();
		assertEquals(1, CollectionUtils.size(list.getAttributes()));
		assertSame(attribute, list.getAttributes().get(0));
		assertEquals(1, CollectionUtils.size(list.getElements()));
		assertEquals(1, CollectionUtils.size(list.getComments()));
		assertEquals("map of [class java.lang.String] and [DummyType]", list.getComments().get(0));
	}
	
	/**
	 * Tests handling a parameterized type when the raw type is not a class 
	 */
	public void testHandleParametrizedTypeWhenRawNotClass() {
		ConfigWriterFromClass writer = new ConfigWriterFromClass(Object.class);
		AttributeSerialization attribute = new AttributeSerialization();
		Serialization ser = writer.handleType("invalid", new ParameterizedType() {
			public Type getRawType() {return new Type() {};}
			public Type getOwnerType() {return null;}
			public Type[] getActualTypeArguments() {return null;}
		}, attribute);
		assertTrue(ser instanceof NameSerialization);
		NameSerialization name = (NameSerialization) ser;
		assertEquals("value", name.getName());
		assertTrue(name.getDelegate() instanceof ComplexSerialization);
		ComplexSerialization complex = (ComplexSerialization) name.getDelegate();
		assertEquals(1, CollectionUtils.size(complex.getAttributes()));
		assertSame(attribute, complex.getAttributes().get(0));
		assertTrue(CollectionUtils.isEmpty(complex.getElements()));
		assertEquals(2, CollectionUtils.size(complex.getComments()));
		assertEquals("Unable to resolve the class for the element [invalid]!", complex.getComments().get(0));
		assertEquals("Its configuration must be written manually.", complex.getComments().get(1));
	}
	
	
	/**
	 * Convert the content of the given resource into a string
	 * @param resource The resource
	 * @return The corresponding string
	 * @throws IOException
	 */
	private static String resourceToString(Resource resource) throws IOException {
    	InputStream is = resource.getInputStream();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        try {
        	String line = null;
        	String sep = "";
            while ((line = reader.readLine()) != null) {
            	sb.append(sep).append(line);
            	sep = "\n";
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }

	/**
	 * A dummy type for testing
	 */
	private static class DummyType implements Type {
		@Override
		public String toString() {
			return "DummyType";
		}
	}
}
