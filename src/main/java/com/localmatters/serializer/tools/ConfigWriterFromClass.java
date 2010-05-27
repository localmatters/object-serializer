package com.localmatters.serializer.tools;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.resolver.BeanUtilsPropertyResolver;
import com.localmatters.serializer.resolver.ComplexPropertyResolver;
import com.localmatters.serializer.resolver.PropertyResolver;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.util.ReflectionUtils;
import com.localmatters.serializer.util.SerializationUtils;
import com.localmatters.serializer.writer.Writer;
import com.localmatters.serializer.writer.XMLWriter;

/**
 * A tools to write a default serialization configuration for a class
 */
public class ConfigWriterFromClass {
	private Writer writer;
	private PropertyResolver resolver;
	private SerializationContext context;

	/**
	 * Default constructor
	 */
	public ConfigWriterFromClass() {
		writer = new XMLWriter();
		BeanUtilsPropertyResolver beanUtils = new BeanUtilsPropertyResolver();
		beanUtils.setIndexedMappedRemoverPattern(Pattern.compile("^([^\\[\\{]+)(?:\\[|\\{).*$"));
		ComplexPropertyResolver complex = new ComplexPropertyResolver();
		complex.setToken(".");
		complex.setDelegate(beanUtils);
		resolver = complex;
		context = new SerializationContext(writer, null, resolver, true);
	}
	
	
	/**
	 * Returns the configuration for the given class
	 * @param className The name of the class to generate the configuration for
	 * @return The default serialization configuration for this class
	 * @throws Exception When the class cannot be found
	 */
	public String getConfiguration(String className) throws Exception {
		Class<?> klass = this.getClass().getClassLoader().loadClass(className);

		ComplexSerialization root = SerializationUtils.createComplex("serializations");
		ComplexSerialization complex = getConfigurationForClass(klass, new Stack<Class<?>>());
		complex.getAttributes().add(0, SerializationUtils.createPropertyAttribute("name", "simpleName"));
		complex.getAttributes().add(0, SerializationUtils.createPropertyAttribute("id", "simpleName"));
		root.addElement(complex);

		return writer.writeRoot(root, klass, context);
	}
	
	/**
	 * Gets the serialization for a class
	 * @param klass The class
	 * @param stack The stack of the parent classes to test for loop
	 * @return The corresponding serialization
	 */
	private ComplexSerialization getConfigurationForClass(Class<?> klass, Stack<Class<?>> stack) {
		if (stack.contains(klass)) {
			throw new RuntimeException("Unable to generate the configuration: unsupported loop of classes encountered!");
		}
		stack.push(klass);
		
		ComplexSerialization complex = SerializationUtils.createComplex("complex");
		complex.addComment("Configuration for the class [" + klass.getName() + "]");

		// loops over the getter
		for (Method getter : ReflectionUtils.getGetters(klass)) {
			String field = ReflectionUtils.getGetterFieldName(getter);
			
			// value
			if (ReflectionUtils.isSimple(getter.getReturnType())) {
				ComplexSerialization value = SerializationUtils.createComplex("value");
				complex.addElement(value);
				value.addAttribute(SerializationUtils.createConstantAttribute("name", field));
				value.addAttribute(SerializationUtils.createConstantAttribute("property", field));
			} 
			// map
			else if (Map.class.isAssignableFrom(getter.getReturnType())) {
				Class<?>[] classes = ReflectionUtils.getGenericClassesForType(getter.getGenericReturnType());
				if ((classes != null) && (classes.length == 2)) {
					
					// simple map
					if (ReflectionUtils.isSimple(classes[0])) {
						ComplexSerialization map = SerializationUtils.createComplex("map");
						complex.addElement(map);
						map.addAttribute(SerializationUtils.createConstantAttribute("name", field));
						map.addAttribute(SerializationUtils.createConstantAttribute("property", field));
						map.addElement(getConfigurationForClass(classes[1], stack));
					} 
					// complex map => list
					else {
						ComplexSerialization list = SerializationUtils.createComplex("list");
						complex.addElement(list);
						list.addComment("Configured the complex map returned by [" + getter.getName() + "] as a list.");
						list.addComment("You can change this to a <map> by setting its [key] attribute");
						list.addAttribute(SerializationUtils.createConstantAttribute("name", field));
						list.addAttribute(SerializationUtils.createConstantAttribute("property", field));
						
						ComplexSerialization element = SerializationUtils.createComplex("complex");
						list.addElement(element);
						ComplexSerialization key = getConfigurationForClass(classes[0], stack);
						element.addElement(key);
						key.addAttribute(SerializationUtils.createConstantAttribute("name", "key"));
						key.addAttribute(SerializationUtils.createConstantAttribute("property", "key"));
						ComplexSerialization value = getConfigurationForClass(classes[1], stack);
						element.addElement(value);
						value.addAttribute(SerializationUtils.createConstantAttribute("name", "value"));
						value.addAttribute(SerializationUtils.createConstantAttribute("property", "value"));

					}
				}
				
			}
			
		}
		
		stack.pop();
		return complex;
	}
	
	
	/**
	   * Get the underlying class for a type, or null if the type is a variable type.
	   * @param type the type
	   * @return the underlying class
	   */
	@SuppressWarnings("unchecked")
	protected static Class<?> getClass(Type type) {
	    if (type instanceof Class) {
	      return (Class) type;
	    }
	    else if (type instanceof ParameterizedType) {
	      return getClass(((ParameterizedType) type).getRawType());
	    }
	    else if (type instanceof GenericArrayType) {
	      Type componentType = ((GenericArrayType) type).getGenericComponentType();
	      Class<?> componentClass = getClass(componentType);
	      if (componentClass != null ) {
	        return Array.newInstance(componentClass, 0).getClass();
	      }
	      else {
	        return null;
	      }
	    }
	    else {
	      return null;
	    }
	  }	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*		
		ConfigWriterFromClass writer = new ConfigWriterFromClass();
		System.out.println(writer.getConfiguration(DummyObject.class.getName()));
		Map<String, Double> map = new HashMap<String, Double>();
//		ParameterizedType parameterizedType = (ParameterizedType) map.getClass().getTypeParameters()[0];
	    System.out.println(map.getClass().getTypeParameters()[0].getClass().getName());
	    
	    Method method = DummyObject.class.getMethod("getOrders", null);

	    System.out.println(Iterable.class.isAssignableFrom(method.getReturnType()));
	    
	    Type returnType = method.getGenericReturnType();

	    if (returnType instanceof ParameterizedType){
	        ParameterizedType type = (ParameterizedType) returnType;
	        Type[] typeArguments = type.getActualTypeArguments();
	        for (Type typeArgument : typeArguments){
	            Class typeArgClass = (Class) typeArgument;
	            System.out.println("typeArgClass = " + typeArgClass);
	        }
	    }

 */
	}

}
