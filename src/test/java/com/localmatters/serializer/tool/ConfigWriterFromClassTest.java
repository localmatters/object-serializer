package com.localmatters.serializer.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
	 * Tests getting the configuration for <code>String</code>
	 * class
	 */
	public void testConfigForString() throws Exception {
		Resource resource = new ClassPathResource("test-string-config.xml");
		assertEquals(resourceToString(resource), ConfigWriterFromClass.getConfiguration(String.class));
	}

	/**
	 * Tests getting the configuration for multiple classes
	 */
	public void testConfigForObjectWithGenericsDummyObjectAndString() throws Exception {
		Resource resource = new ClassPathResource("test-multiple-config.xml");
		assertEquals(resourceToString(resource), ConfigWriterFromClass.getConfiguration(ObjectWithGenerics.class, DummyObject.class, String.class, ChildOfSelfreferencingObject.class));
	}
	
	/**
	 * Convert the content of the given resouce into a string
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

	
}
