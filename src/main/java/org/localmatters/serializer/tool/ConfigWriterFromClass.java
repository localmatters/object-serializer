/*
   Copyright 2010-present Local Matters, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.localmatters.serializer.tool;

import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_BEAN;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_ID;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_NAME;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PROPERTY;
import static org.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_TARGET;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_COMPLEX;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_LIST;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_MAP;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_REFERENCE;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_ROOT;
import static org.localmatters.serializer.config.SerializationElementHandler.TYPE_VALUE;
import static org.localmatters.serializer.util.SerializationUtils.createComplex;
import static org.localmatters.serializer.util.SerializationUtils.createConstantAttribute;
import static org.localmatters.serializer.util.SerializationUtils.createName;
import static org.localmatters.serializer.util.SerializationUtils.getSingular;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.resolver.BeanUtilsPropertyResolver;
import org.localmatters.serializer.resolver.ComplexPropertyResolver;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.util.ReflectionUtils;
import org.localmatters.serializer.writer.XMLWriter;


/**
 * A tools to write a default serialization configuration for a class
 */
public class ConfigWriterFromClass {
	private static final Logger LOGGER = Logger.getLogger(ConfigWriterFromClass.class);
	private static final String INDEXED_AND_MAP_REMOVE_PATTERN = "^([^\\[\\{]+)(?:\\[|\\{).*$";
	private Class<?>[] rootClasses;
	private ComplexSerialization root;
	private Map<Class<?>, NameSerialization> references;
	private Map<Class<?>, String> ids;

	/**
	 * Constructor with the specification of the class to build the 
	 * serialization configuration from
	 * @param klass The class
	 */
	protected ConfigWriterFromClass(Class<?> ...classes) {
		setRootClasses(classes);
	}

	/**
	 * Analyses the klass and returns the serialization to generate the 
	 * basic configuration to serialize its instances.
	 * @return The serialization to generate the basic configuration
	 */
	protected Serialization analyse() {
		ids = new HashMap<Class<?>, String>();
		root = createComplex(
				createConstantAttribute("xmlns", "http://schema.localmatters.org/serializations"),
				createConstantAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"),
				createConstantAttribute("xsi:schemaLocation", "http://schema.localmatters.org/serializations http://schema.localmatters.org/serializations.xsd"));
		references = new HashMap<Class<?>, NameSerialization>();
		for (Class<?> klass : getRootClasses()) {
			references.put(klass, null);
		}

		for (int i=getRootClasses().length; i>0; i--) {
			Class<?> rootClass = getRootClasses()[i-1];
			String name = rootClass.getSimpleName();
			root.getElements().add(0, handleClass(name, rootClass, 
					createConstantAttribute(ATTRIBUTE_ID, getIdForClass(rootClass)), 
					createConstantAttribute(ATTRIBUTE_BEAN, name),
					createConstantAttribute(ATTRIBUTE_NAME, name)));
		}
		
		return createName(TYPE_ROOT, root);
	}
	
	/**
	 * Gets the ID for the given class
	 * @param klass The class
	 * @return The id for this class
	 */
	protected String getIdForClass(Class<?> klass) {
		String id = ids.get(klass);
		if (id == null) {
			id = klass.getSimpleName();
			if (ids.containsValue(id)) {
				id = klass.getName();
			}
			ids.put(klass, id);
		}
		return id;
	}
	
	/**
	 * Handles the given type
	 * @param name The name of this element
	 * @param type The type to handle
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected Serialization handleType(String name, Type type, Serialization...attributes) {
		LOGGER.debug("TYPE - name [" + name + "] type [" + type + "]");
		Class<?> klass = null;
		
		// first, resolves the class
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			Type rawType = pType.getRawType();
			if (rawType instanceof Class<?>) {
				klass = (Class<?>) rawType;
			}
		} else if (type instanceof Class<?>) {
			klass = (Class<?>) type;
		}
		
		// unable to resolve the class
		if (klass == null) {
			ComplexSerialization value = createComplex(attributes);
			value.addComment("Unable to resolve the class for the element [" + name + "]!");
			value.addComment("Its configuration must be written manually.");
			return createName(TYPE_VALUE, value);
		}

		// handles simple class
		if (ReflectionUtils.isSimple(klass)) {
			return handleSingle(name, klass, attributes);
		}
		
		// handles map
		if (Map.class.isAssignableFrom(klass)) {
			return handleMap(name, klass, ReflectionUtils.getTypeArgumentsForType(type), attributes);
		}
		
		// handles iterable
		if (Iterable.class.isAssignableFrom(klass)) {
			return handleIterator(name, klass, ReflectionUtils.getTypeArgumentsForType(type), attributes);
		}
		// handles array
		if (klass.isArray()) {
			return handleIterator(name, klass, ReflectionUtils.getTypeArgumentsForType(type), attributes);
		}
		
		return handleReference(name, klass, attributes);
		
	}
	
	/**
	 * This method handles the case where one class is at more than one place
	 * @param name The name of this element
	 * @param klass The class to handle
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected NameSerialization handleReference(String name, Class<?> klass, Serialization...attributes) {
		LOGGER.debug("REFERENCE - name [" + name + "] class [" + klass + "]");
		boolean setAdReference = references.containsKey(klass);

		NameSerialization ref = null;
		if (!setAdReference) {
			// to avoid cycle, we put a temporary name in the references map 
			references.put(klass, createName("avoidCycle", null));
			ref = handleClass(name, klass, attributes);
			
			// if the value in the reference map is null, another occurence of
			// this class has been encountered
			setAdReference = (references.get(klass) == null);
			references.put(klass, ref);
		}
		
		if (setAdReference) {
			String id = getIdForClass(klass);
			ref = references.get(klass);
			
			// this is the second time this class is encountered, so we add its
			// configuration to the root and replaces its "in place" 
			// configuration by a reference
			if (ref != null) {
				ComplexSerialization delegate = (ComplexSerialization) ref.getDelegate();
				if (delegate != null) {
					List<Serialization> referencedAttributes = delegate.getAttributes();
					delegate.setAttributes(new ArrayList<Serialization>());
					delegate.addAttribute(createConstantAttribute(ATTRIBUTE_ID, id));
					delegate.addAttribute(createConstantAttribute(ATTRIBUTE_NAME, klass.getSimpleName()));
					root.addElement(createName(ref.getName(), delegate));
					
					delegate = new ComplexSerialization();
					ref.setName(TYPE_REFERENCE);
					ref.setDelegate(delegate);
					delegate.setAttributes(referencedAttributes);
					delegate.addAttribute(createConstantAttribute(ATTRIBUTE_TARGET, id));
				}
				references.put(klass, null);
			}

			// then creates a new reference to return
			ComplexSerialization reference = createComplex(attributes);
			reference.addAttribute(createConstantAttribute(ATTRIBUTE_TARGET, id));
			return createName(TYPE_REFERENCE, reference);
		}
		
		return ref;
	}	
	
	/**
	 * Handles a map
	 * @param name The name of this element
	 * @param klass The class
	 * @param types The types of the arguments of this class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected NameSerialization handleMap(String name, Class<?> klass, Type[] types, Serialization...attributes) {
		LOGGER.debug("MAP - name [" + name + "] class [" + klass + "]");
		String type = TYPE_MAP;
		ComplexSerialization map = createComplex(attributes);
		String singleName = getSingular(name, "entry");

		if ((types != null) && (types.length == 2)) {
			
			// simple map
			if ((types[0] instanceof Class<?>) && ReflectionUtils.isNonNumericSimple((Class<?>) types[0])) {
				if (!((types[1] instanceof Class<?>) && ReflectionUtils.isSimple((Class<?>) types[1]))) {
					map.addComment("map of [" + types[0] + "] and [" + types[1] + "]");
					map.addElement(handleType(singleName, types[1]));
				}
			} 
			// complex map => list
			else {
				type = TYPE_LIST;
				map.addComment("Configured the complex map returned by [" + name + "] as a list.");
				map.addComment("This can change to a <map> by setting its [key] attribute to the");
				map.addComment("property of the key object that identifies it.");
				ComplexSerialization element = createComplex(createConstantAttribute(ATTRIBUTE_NAME, singleName));
				element.addElement(handleType("key", types[0], 
						createConstantAttribute(ATTRIBUTE_PROPERTY, "key")));
				element.addElement(handleType("value", types[1], 
						createConstantAttribute(ATTRIBUTE_PROPERTY, "value")));
				map.addElement(createName(TYPE_COMPLEX, element));
			}
		}
		
		// map of unknown type
		else {
			map.addComment("Unable to identify the types for the [" + name + "] map!");
			map.addComment("The configuration of its entries must be written manually.");
		}
		
		return createName(type, map);
	}
	
	/**
	 * Handles an iterator
	 * @param name The name of this element
	 * @param klass The class
	 * @param types The types of the arguments of this class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected NameSerialization handleIterator(String name, Class<?> klass, Type[] types, Serialization...attributes) {
		LOGGER.debug("ITERATOR - name [" + name + "] class [" + klass + "]");
		ComplexSerialization list = createComplex(attributes);
		String singleName = getSingular(name, "element");

		if ((types != null) && (types.length == 1)) {
			if (!((types[0] instanceof Class<?>) && ReflectionUtils.isSimple((Class<?>) types[0]))) {
				list.addComment("Iteration of [" + types[0] + "]");
				list.addElement(handleType(singleName, types[0], createConstantAttribute(ATTRIBUTE_NAME, singleName)));
			}
		}
		
		// list of unknown type
		else {
			list.addComment("Unable to identify the types for the [" + name + "] iteration!");
			list.addComment("The configuration of its elements must be written manually.");
			list.addElement(createComplex(TYPE_VALUE, createConstantAttribute(ATTRIBUTE_NAME, singleName)));
		}
		
		return createName(TYPE_LIST, list);
	}
	
	/**
	 * Handles a single class
	 * @param name The name of this element
	 * @param klass The class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected NameSerialization handleSingle(String name, Class<?> klass, Serialization...attributes) {
		LOGGER.debug("SINGLE - name [" + name + "] class [" + klass + "]");
		return createName(TYPE_VALUE, createComplex(attributes));
	}
	
	
	/**
	 * Handles the given class
	 * @param name The name of this element
	 * @param klass The class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected NameSerialization handleClass(String name, Class<?> klass, Serialization...attributes) {
		LOGGER.debug("CLASS - name [" + name + "] class [" + klass + "]");
		ComplexSerialization complex = createComplex(attributes);
		complex.addComment(klass.getName());

		// loops over the getter
		for (Method getter : ReflectionUtils.getGetters(klass)) {
			String field = ReflectionUtils.getGetterFieldName(getter);
			complex.addElement(handleType(field, getter.getGenericReturnType(),
						createConstantAttribute(ATTRIBUTE_PROPERTY, field)));
		}
		
		return createName(TYPE_COMPLEX, complex);
	}
	
	/**
	 * @return The root classes
	 */
	public Class<?>[] getRootClasses() {
		return rootClasses;
	}

	/**
	 * @param rootClasses The root classes
	 */
	public void setRootClasses(Class<?>[] rootClasses) {
		this.rootClasses = rootClasses;
	}

	/**
	 * Returns the serialization to use to generate the basic configuration for
	 * the given classes
	 * @param classes The classes to build the serialization for
	 * @return The serialization to generate the basic serialization 
	 * configuration for instances of the given class
	 */
	protected static Serialization getSerialization(Class<?>...classes) {
		ConfigWriterFromClass configWriter = new ConfigWriterFromClass(classes);
		return configWriter.analyse();
	}	

	/**
	 * Returns the basic configuration to serialize instances of some classes 
	 * @param className The names of the classes
	 * @return The configuration
	 * @throws ClassNotFoundException When the class cannot be found
	 * @throws SerializationException When configuration cannot be generated
	 */
	public static String getConfiguration(String...classNames) throws ClassNotFoundException, SerializationException {
		Class<?>[] classes = new  Class<?>[classNames.length];
		for (int i=0; i<classNames.length; i++) {
			classes[i] = ConfigWriterFromClass.class.getClassLoader().loadClass(classNames[i]);
		}
		return getConfiguration(classes);
	}

	/**
	 * Returns the basic configuration to serialize instances of some classes 
	 * @param classes The classes to return the serialization configuration for
	 * @return The configuration
	 * @throws ClassNotFoundException When the class cannot be found
	 * @throws SerializationException When configuration cannot be generated
	 */
	public static String getConfiguration(Class<?>...classes) throws ClassNotFoundException, SerializationException {
		XMLWriter writer = new XMLWriter();
		BeanUtilsPropertyResolver beanUtils = new BeanUtilsPropertyResolver();
		beanUtils.setIndexedMappedRemoverPattern(Pattern.compile(INDEXED_AND_MAP_REMOVE_PATTERN));
		ComplexPropertyResolver resolver = new ComplexPropertyResolver();
		resolver.setToken(".");
		resolver.setDelegate(beanUtils);
		SerializationContext ctx = new SerializationContext(writer, resolver, new ByteArrayOutputStream());
		ctx.setFormatting(true);
		Serialization serialization = getSerialization(classes);
		writer.writeRoot(serialization, classes, ctx);
		return ctx.getOutputStream().toString();
	}
}
