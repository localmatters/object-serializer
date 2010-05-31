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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.resolver.BeanUtilsPropertyResolver;
import com.localmatters.serializer.resolver.ComplexPropertyResolver;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.util.ReflectionUtils;
import com.localmatters.serializer.util.SerializationUtils;
import com.localmatters.serializer.writer.XMLWriter;
import com.localmatters.util.StringUtils;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

/**
 * A tools to write a default serialization configuration for a class
 */
public class ConfigWriterFromClass {
	private static final String INDEXED_AND_MAP_REMOVE_PATTERN = "^([^\\[\\{]+)(?:\\[|\\{).*$";
	private static final Pattern COLLECTION_PATTERN = Pattern.compile("^(?:paM|tsiL|xednI|teS|yarrA|noitcelloC)(.*)");
	private static final Pattern PLURAL_PATTERN = Pattern.compile("^(?:([^s]*[A-Z])s|s)(?:(e[iaou]|[a-df-rt-z])|e)(.*)");
	private Class<?> rootClass;
	private ComplexSerialization root;
	private Map<Class<?>, ReferenceSerialization> references;
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
		references = new HashMap<Class<?>, ReferenceSerialization>();
		references.put(getRootClass(), null);
		ids = new HashMap<Class<?>, String>();
		root = SerializationUtils.createComplex(TYPE_ROOT);
		
		String name = getRootClass().getSimpleName();
		root.getElements().add(0, handleClass(name, getRootClass(), 
				SerializationUtils.createConstantAttribute(ATTRIBUTE_ID, getIdForClass(getRootClass())),
				SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, name)));
		return root;
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
			ComplexSerialization value = SerializationUtils.createComplex(TYPE_VALUE, attributes);
			value.addComment("Unable to resolve the class for the element [" + name + "]!");
			value.addComment("Its configuration must be writtent manually.");
			return value;
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
	protected Serialization handleReference(String name, Class<?> klass, Serialization...attributes) {
		if (references.containsKey(klass)) {
			String id = getIdForClass(klass);
			ReferenceSerialization ref = references.get(klass);
			
			// this is the second time this class is encountered, so we add its
			// configuration to the root and replaces its "in place" 
			// configuration by a reference
			if (ref != null) {
				ComplexSerialization referenced = (ComplexSerialization) ref.getReferenced();
				List<Serialization> referencedAttributes = referenced.getAttributes();
				referenced.setAttributes(new ArrayList<Serialization>());
				referenced.addAttribute(SerializationUtils.createConstantAttribute(ATTRIBUTE_ID, id));
				referenced.addAttribute(SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, name));
				root.addElement(referenced);
				
				referenced = SerializationUtils.createComplex(TYPE_REFERENCE);
				ref.setReferenced(referenced);
				referenced.setAttributes(referencedAttributes);
				referenced.addAttribute(SerializationUtils.createConstantAttribute(ATTRIBUTE_TARGET, id));				
				references.put(klass, null);
			}

			// then creates a new reference to return
			ComplexSerialization reference = SerializationUtils.createComplex(TYPE_REFERENCE, attributes);
			reference.addAttribute(SerializationUtils.createConstantAttribute(ATTRIBUTE_TARGET, id));
			return reference;
		}
		
		// otherwise, encapsulate the serialization into a reference
		ReferenceSerialization ref = SerializationUtils.createReference(handleClass(name, klass, attributes));
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
	protected Serialization handleMap(String name, Class<?> klass, Type[] types, Serialization...attributes) {
		ComplexSerialization map = null;
		String singleName = getSingular(name, "entry");

		if ((types != null) && (types.length == 2)) {
			
			// simple map
			if ((types[0] instanceof Class<?>) && ReflectionUtils.isSimple((Class<?>) types[0])) {
				map = SerializationUtils.createComplex(TYPE_MAP, attributes);
				map.addComment("map of [" + types[0] + "] and [" + types[1] + "]");
				Serialization element = handleType(singleName, types[1], 
						SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, singleName));
				map.addElement(element);
			} 
			// complex map => list
			else {
				map = SerializationUtils.createComplex(TYPE_LIST, attributes);
				map.addComment("Configured the complex map returned by [" + name + "] as a list.");
				map.addComment("This can change to a <map> by setting its [key] attribute to the");
				map.addComment("property of the key object that identifies it.");
				ComplexSerialization element = SerializationUtils.createComplex(TYPE_COMPLEX);
				map.addElement(element);
				element.addElement(handleType("key", types[0],
						SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, "key"),
						SerializationUtils.createConstantAttribute(ATTRIBUTE_PROPERTY, "key")));
				element.addElement(handleType("value", types[1],
						SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, "value"),
						SerializationUtils.createConstantAttribute(ATTRIBUTE_PROPERTY, "value")));
			}
		}
		
		// map of unknown type
		else {
			map = SerializationUtils.createComplex(TYPE_MAP, attributes);
			map.addComment("Unable to identify the types for the [" + name + "] map!");
			map.addComment("The configuration of its entries must be writtent manually.");
			map.addElement(SerializationUtils.createComplex(TYPE_VALUE,
					SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, singleName)));
		}
		
		return map;
	}
	
	/**
	 * Handles an iterator
	 * @param name The name of this element
	 * @param klass The class
	 * @param types The types of the arguments of this class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected Serialization handleIterator(String name, Class<?> klass, Type[] types, Serialization...attributes) {
		ComplexSerialization list = null;
		String singleName = getSingular(name, "element");

		if ((types != null) && (types.length == 1)) {
			list = SerializationUtils.createComplex(TYPE_LIST, attributes);
			list.addComment("List of [" + types[0] + "]");
			list.addElement(handleType(singleName, types[0],
					SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, singleName)));
		}
		
		// list of unknown type
		else {
			list = SerializationUtils.createComplex(TYPE_LIST, attributes);
			list.addComment("Unable to identify the types for the [" + name + "] list!");
			list.addComment("The configuration of its entries must be writtent manually.");
			list.addElement(SerializationUtils.createComplex(TYPE_VALUE,
					SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, singleName)));
		}
		
		return list;
	}
	
	/**
	 * Handles a single class
	 * @param name The name of this element
	 * @param klass The class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected Serialization handleSingle(String name, Class<?> klass, Serialization...attributes) {
		return SerializationUtils.createComplex(TYPE_VALUE, attributes);
	}
	
	
	/**
	 * Handles the given class
	 * @param name The name of this element
	 * @param klass The class
	 * @param attributes The attributes to add to the serialization
	 * @return The corresponding serialization
	 */
	protected Serialization handleClass(String name, Class<?> klass, Serialization...attributes) {
		ComplexSerialization complex = SerializationUtils.createComplex(TYPE_COMPLEX, attributes);
		complex.addComment(klass.getName());

		// loops over the getter
		for (Method getter : ReflectionUtils.getGetters(klass)) {
			String field = ReflectionUtils.getGetterFieldName(getter);
			complex.addElement(handleType(field, getter.getGenericReturnType(),
						SerializationUtils.createConstantAttribute(ATTRIBUTE_NAME, field),
						SerializationUtils.createConstantAttribute(ATTRIBUTE_PROPERTY, field)));
		}
		
		return complex;
	}

	/**
	 * Returns the singular value for the given term
	 * @param term The term
	 * @param def The default value to return if no singular could be found
	 * @return The singular value for this term or 
	 */
	protected String getSingular(String term, String def) {
		String result = "";
		String str = StringUtils.reverse(term);
		
		// first, lets clean the List or Map at the end
		Matcher m = COLLECTION_PATTERN.matcher(str);
		if (m.matches()) {
			if (m.matches()) {
				str = m.group(1);
				result = StringUtils.reverse(str);
			}
		}
		
		// then we look for plural
		m = PLURAL_PATTERN.matcher(str);
		if (m.matches()) {
			result = "";
			if (m.group(3) != null) {
				result += StringUtils.reverse(m.group(3));
			}
			if (m.group(2) != null) {
				if ("ei".equals(m.group(2))) {
					result += "y";
				} else {
					result += StringUtils.reverse(m.group(2));
				}
			}
			if (m.group(1) != null) {
				result += StringUtils.reverse(m.group(1));
			}
		}
		return StringUtils.defaultIfEmpty(result, def);
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
		SerializationContext ctx = new SerializationContext(writer, resolver, new ByteOutputStream());
		ctx.setFormatting(true);
		Serialization serialization = getSerialization(klass);
		writer.writeRoot(serialization, klass, ctx);
		return ctx.getOutputStream().toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(getConfiguration("com.localmatters.serializer.test.domain.ObjectWithGenerics"));
	}
	
}
