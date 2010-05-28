package com.localmatters.serializer.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.localmatters.util.ArrayUtils;

/**
 * Class offering utils methods to reflect upon objects and classes
 */
public abstract class ReflectionUtils {
	private static Pattern GETTER_PATTERN = Pattern.compile("^(?:get|is|has)([A-Z]|\\d)(\\w*)?$");
	private static Set<String> GETTERS_TO_EXCLUDE;
	private static Map<Class<?>, Class<?>> WRAPPER_2_PRIMITIVE_MAP;
	private static final Set<Class<?>> NUMBERS;
	
	/**
	 * Initialize the constants
	 */
	static {
		GETTERS_TO_EXCLUDE = new HashSet<String>();
		GETTERS_TO_EXCLUDE.add("getClass");
		
		WRAPPER_2_PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>();
		WRAPPER_2_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Float.class, float.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Long.class, long.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Integer.class, int.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Short.class, short.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Byte.class, byte.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Double.class, double.class);
		WRAPPER_2_PRIMITIVE_MAP.put(Character.class, char.class);
		
		NUMBERS = new HashSet<Class<?>>();
		NUMBERS.add(Float.class);
		NUMBERS.add(float.class);
		NUMBERS.add(Long.class);
		NUMBERS.add(long.class);
		NUMBERS.add(Integer.class);
		NUMBERS.add(int.class);
		NUMBERS.add(Short.class);
		NUMBERS.add(short.class);
		NUMBERS.add(Byte.class);
		NUMBERS.add(byte.class);
		NUMBERS.add(Double.class);
		NUMBERS.add(double.class);
		
	}
	
	/**
	 * Returns the primitive class corresponding to the given primitive wrapper
	 * @param wrapper The wrapper class
	 * @return The corresponding primitive class or null if the given class is
	 * not a primitive wrapper
	 */
    public static Class<?> getPrimitiveClass(Class<?> wrapper) {
    	return WRAPPER_2_PRIMITIVE_MAP.get(wrapper);
    }
	
    /**
     * Whether the given class is a primitive number (<code>float</code>, 
     * <code>int</code>, <code>long</code>, <code>short</code>, 
     * <code>double</code> or <code>byte</code>) or one of their corresponding 
     * wrapper class
     * @param klass The class to evaluate
     * @return True if the class is a primitive (or primitive wrapper) number
     */
    public static boolean isNumeric(Class<?> klass) {
    	return NUMBERS.contains(klass);
    }
	
    /**
     * Returns whether the given class is the primitive <code>boolean</code> or
     * its corresponding wrapper class: <code>Boolean</code>
     * @param klass The class to evaluate
     * @return True if the class is <code>boolean</code> or <code>Boolean</code>
     */
    public static boolean isBoolean(Class<?> klass) {
    	return Boolean.class.equals(klass) || boolean.class.equals(klass);
    }
	
	/**
	 * Indicates whether the class is a primitive type (e.g. boolean) or 
	 * primitive wrapper (e.g. Boolean)
	 * @param klass The class to evaluate
	 * @return True if the class is a primitive or a primitive wrapper
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> klass) {
		return klass.isPrimitive() || (WRAPPER_2_PRIMITIVE_MAP.containsKey(klass));
	}
	
	/**
	 * Returns whether the instances of the given class can be represent by a
	 * simple value; i.e whether this is a primitive or a primitive wrapper, a
	 * string, a date, a enumeration, etc.
	 * @param klass
	 * @return
	 */
	public static boolean isSimple(Class<?> klass) {
		return isPrimitiveOrWrapper(klass) || 
			(String.class.equals(klass)) || 
			(Date.class.isAssignableFrom(klass)) || 
			(klass.isEnum());
	}

	/**
	 * Returns the return class arguments for the given type if its is or is
	 * parent is generic. By example, this method will return the classes
	 * <code>Double</code> and <code>String</code> for the type
	 * <code>Map&lt;Double, String&gt;</code>. Returns null otherwise
	 * @param type The type
	 * @return The list of the return type arguments or null
	public static Class<?>[] getGenericClassesForType(Type type) {
		if (type instanceof ParameterizedType){
	        ParameterizedType paramType = (ParameterizedType) type;
	        Type[] args = paramType.getActualTypeArguments();
	        Class<?>[] classes = new Class<?>[args.length];
	        for (int i=0; i<args.length; i++) {
	        	Type arg = args[i];
	        	if (arg instanceof Class<?>) {
	        		classes[i] = (Class<?>) arg;
	        	} else {
	        		// this implies that the generic classes of this type cannot
	        		// be resolved
	        		return null;
	        	}
	        }

	        return classes;
	    }
		if (type instanceof Class<?>) {
			return getGenericClassesForType(((Class<?>) type).getGenericSuperclass());
		}
	    return null;
	}
	 */

	/**
	 * Returns the return the type arguments of the given type.
	 * @param type The type
	 * @return The list of the return type arguments or null
	 */
	public static Type[] getTypeArgumentsForType(Type type) {
		if (type instanceof ParameterizedType){
	        ParameterizedType paramType = (ParameterizedType) type;
	        return paramType.getActualTypeArguments();
	    }
		if (type instanceof Class<?>) {
			return getTypeArgumentsForType(((Class<?>) type).getGenericSuperclass());
		}
	    return null;
	}

	/**
	 * Returns the getter methods (getXXX, isXXX, hasXXX) for the given class
	 * @param klass The class
	 * @return The getters of this class sorted by name
	 */
	public static Collection<Method> getGetters(Class<?> klass) {
		Map<String, Method> getters = new TreeMap<String, Method>();
		Method[] methods = klass.getMethods();
		for (Method method : methods) {
			if (ArrayUtils.isEmpty(method.getTypeParameters())) {
				Matcher matcher = GETTER_PATTERN.matcher(method.getName());
				if (matcher.find() && !GETTERS_TO_EXCLUDE.contains(method.getName())) {
					getters.put(method.getName(), method);
				}
			}
		}
		return getters.values();
	}
	
	/**
	 * Returns the field name (starting by a lower case and without the get, is
	 * or has prefix) for the given getter method
	 * @param method The method to get the clean name of
	 * @return The clean name for this method or null if the method is not a 
	 * getter method
	 */
	public static String getGetterFieldName(Method method) {
		Matcher matcher = GETTER_PATTERN.matcher(method.getName());
		if (matcher.find() && !GETTERS_TO_EXCLUDE.contains(method.getName())) {
			String first = matcher.group(1).toLowerCase();
			String rest = matcher.group(2);
			if (StringUtils.isNotBlank(rest)) {
				return first + rest;
			}
			return first;
		}
		return null;
	}
}
