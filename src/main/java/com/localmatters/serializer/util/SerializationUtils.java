/**
 * 
 */
package com.localmatters.serializer.util;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;

/**
 * Class offering utils methods to create, work with <code>Serialization</code>.
 */
public abstract class SerializationUtils {

	/**
	 * Creates a new attribute serialization with the given name
	 * @param name The name of the serialization
	 * @return The corresponding serialization
	 */
	public static AttributeSerialization createAttribute(String name) {
		AttributeSerialization serialization = new AttributeSerialization();
		serialization.setName(name);
		return serialization;
	}

	/**
	 * Creates a new attribute serialization with the given name and resolving
	 * it value from the given property
	 * @param name The name of the serialization
	 * @param property The property
	 * @return The corresponding serialization
	 */
	public static PropertySerialization createPropertyAttribute(String name, String property) {
		PropertySerialization serialization = new PropertySerialization();
		serialization.setProperty(property);
		serialization.setDelegate(createAttribute(name));
		return serialization;
	}

	/**
	 * Creates a new attribute serialization with the given name and constant
	 * value
	 * @param name The name of the serialization
	 * @param constant The constant
	 * @return The corresponding serialization
	 */
	public static ConstantSerialization createConstantAttribute(String name, Object constant) {
		ConstantSerialization serialization = new ConstantSerialization();
		serialization.setConstant(constant);
		serialization.setDelegate(createAttribute(name));
		return serialization;
	}

	/**
	 * Creates a new complex serialization with the given name
	 * @param name The name of the serialization
	 * @return The corresponding serialization
	 */
	public static ComplexSerialization createComplex(String name) {
		ComplexSerialization serialization = new ComplexSerialization();
		serialization.setName(name);
		return serialization;
	}
	
}