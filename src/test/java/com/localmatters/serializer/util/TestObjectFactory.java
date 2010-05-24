package com.localmatters.serializer.util;

import java.util.HashMap;

import org.apache.commons.lang.Validate;

import com.localmatters.util.objectfactory.ClassLookupObjectFactory;
import com.localmatters.util.objectfactory.ClassNameKeyStrategy;

/**
 * Object factory to use for testing
 */
public class TestObjectFactory extends ClassLookupObjectFactory {
	
	/**
	 * Default constructor
	 */
    @SuppressWarnings("unchecked")
	public TestObjectFactory() {
        super();
        setClassMap(new HashMap<String, Class>());
        setUseClassWhenLookupFails(true);
        ClassNameKeyStrategy strategy = new ClassNameKeyStrategy();
        strategy.setIncludePackageName(false);
        setKeyStrategy(strategy);
    }
    
    /**
     * Adds the given class
     * @param keyClass The key class
     * @param classToCreate The class to create
     */
    @SuppressWarnings("unchecked")
	public void add(Class keyClass, Class classToCreate) {
        String key = getKeyStrategy().generateKey(keyClass);
        Validate.notNull(key);
        getClassMap().put(key, classToCreate);
    }
}
