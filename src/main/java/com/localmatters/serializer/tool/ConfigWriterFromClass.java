package com.localmatters.serializer.tool;

import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_ID;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_NAME;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_PROPERTY;
import static com.localmatters.serializer.config.SerializationElementHandler.ATTRIBUTE_TARGET;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_COMPLEX;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_LIST;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_MAP;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_REFERENCE;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_ROOT;
import static com.localmatters.serializer.config.SerializationElementHandler.TYPE_VALUE;
import static com.localmatters.serializer.util.SerializationUtils.createComplex;
import static com.localmatters.serializer.util.SerializationUtils.createConstantAttribute;
import static com.localmatters.serializer.util.SerializationUtils.createName;
import static com.localmatters.serializer.util.SerializationUtils.getSingular;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.resolver.BeanUtilsPropertyResolver;
import com.localmatters.serializer.resolver.ComplexPropertyResolver;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.util.ReflectionUtils;
import com.localmatters.serializer.writer.XMLWriter;

/**
 * A tools to write a default serialization configuration for a class
 */
public class ConfigWriterFromClass {
	private static final String INDEXED_AND_MAP_REMOVE_PATTERN = "^([^\\[\\{]+)(?:\\[|\\{).*$";
	private Class<?> rootClass;
	private ComplexSerialization root;
	private Map<Class<?>, NameSerialization> references;
	private Map<Class<?>, String> ids;

	/**
	 * Constructor with the specification of the class to build the 
	 * serialization configuration from
	 * @param klass The class
	 */
	protected ConfigWriterFromClass(Class<?> klass) {
		setRootClass(klass);
	}

	/**
	 * Analyses the klass and returns the serialization to generate the 
	 * basic configuration to serialize its instances.
	 * @return The serialization to generate the basic configuration
	 */
	protected Serialization analyse() {
		references = new HashMap<Class<?>, NameSerialization>();
		references.put(getRootClass(), null);
		ids = new HashMap<Class<?>, String>();
		root = new ComplexSerialization();
		
		String name = getRootClass().getSimpleName();
		root.getElements().add(0, handleClass(name, getRootClass(), 
				createConstantAttribute(ATTRIBUTE_ID, getIdForClass(getRootClass())), 
				createConstantAttribute(ATTRIBUTE_NAME, name)));
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
			value.addComment("Its configuration must be writtent manually.");
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
		if (references.containsKey(klass)) {
			String id = getIdForClass(klass);
			NameSerialization ref = references.get(klass);
			
			// this is the second time this class is encountered, so we add its
			// configuration to the root and replaces its "in place" 
			// configuration by a reference
			if (ref != null) {
				ComplexSerialization delegate = (ComplexSerialization) ref.getDelegate();
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
				references.put(klass, null);
			}

			// then creates a new reference to return
			ComplexSerialization reference = createComplex(attributes);
			reference.addAttribute(createConstantAttribute(ATTRIBUTE_TARGET, id));
			return createName(TYPE_REFERENCE, reference);
		}
		
		// otherwise, handle the class
		NameSerialization ref = handleClass(name, klass, attributes);
		references.put(klass, ref);
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
		String type = TYPE_MAP;
		ComplexSerialization map = createComplex(attributes);
		String singleName = getSingular(name, "entry");

		if ((types != null) && (types.length == 2)) {
			
			// simple map
			if ((types[0] instanceof Class<?>) && ReflectionUtils.isSimple((Class<?>) types[0])) {
				if (!((types[1] instanceof Class<?>) && ReflectionUtils.isSimple((Class<?>) types[1]))) {
					map.addComment("map of [" + types[0] + "] and [" + types[1] + "]");
					map.addElement(handleType(singleName, types[1], createConstantAttribute(ATTRIBUTE_NAME, singleName)));
				}
			} 
			// complex map => list
			else {
				type = TYPE_LIST;
				map.addComment("Configured the complex map returned by [" + name + "] as a list.");
				map.addComment("This can change to a <map> by setting its [key] attribute to the");
				map.addComment("property of the key object that identifies it.");
				ComplexSerialization element = new ComplexSerialization();
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
			map.addElement(createComplex(TYPE_VALUE, createConstantAttribute(ATTRIBUTE_NAME, singleName)));
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
			list.addComment("The configuration of its entries must be writtent manually.");
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
	 * @return The root class
	 */
	public Class<?> getRootClass() {
		return rootClass;
	}

	/**
	 * @param rootClass The root class
	 */
	public void setRootClass(Class<?> rootClass) {
		this.rootClass = rootClass;
	}

	/**
	 * Returns the serialization to use to generate the basic configuration for
	 * the given class
	 * @param klass The class to build the serialization for
	 * @return The serialization to generate the basic serialization 
	 * configuration for instances of the given class
	 */
	protected static Serialization getSerialization(Class<?> klass) {
		ConfigWriterFromClass configWriter = new ConfigWriterFromClass(klass);
		return configWriter.analyse();
	}	

	/**
	 * Returns the basic configuration to serialize instances of the given class 
	 * @param className The name of the class
	 * @return The configuration
	 * @throws ClassNotFoundException When the class cannot be found
	 * @throws SerializationException When configuration cannot be generated
	 */
	public static String getConfiguration(String className) throws ClassNotFoundException, SerializationException {
		Class<?> klass = ConfigWriterFromClass.class.getClassLoader().loadClass(className);
		XMLWriter writer = new XMLWriter();
		BeanUtilsPropertyResolver beanUtils = new BeanUtilsPropertyResolver();
		beanUtils.setIndexedMappedRemoverPattern(Pattern.compile(INDEXED_AND_MAP_REMOVE_PATTERN));
		ComplexPropertyResolver resolver = new ComplexPropertyResolver();
		resolver.setToken(".");
		resolver.setDelegate(beanUtils);
		SerializationContext ctx = new SerializationContext(writer, resolver, new ByteArrayOutputStream());
		ctx.setFormatting(true);
		Serialization serialization = getSerialization(klass);
		writer.writeRoot(serialization, klass, ctx);
		return ctx.getOutputStream().toString();
	}
}
