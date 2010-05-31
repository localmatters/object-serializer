/**
 * 
 */
package com.localmatters.serializer.util;

import java.util.List;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;

/**
 * Class offering utils methods to create, work with <code>Serialization</code>.
 */
public abstract class SerializationUtils {

	/**
	 * Creates a new attribute serialization with the given name
	 * @param name The name of the serialization
	 * @return The corresponding serialization
	 */
	public static NameSerialization createAttribute(String name) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(new AttributeSerialization());
		return ser;
	}

	/**
	 * Creates a new attribute serialization resolving its value from the given
	 * property
	 * @param property The property
	 * @return The corresponding serialization
	 */
	public static PropertySerialization createPropertyAttribute(String property) {
		PropertySerialization ser = new PropertySerialization();
		ser.setProperty(property);
		ser.setDelegate(new AttributeSerialization());
		return ser;
	}

	/**
	 * Creates a new attribute serialization with the given name and resolving
	 * it value from the given property
	 * @param name The name of the serialization
	 * @param property The property
	 * @return The corresponding serialization
	 */
	public static NameSerialization createAttribute(String name, String property) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(createPropertyAttribute(property));
		return ser;
	}

	/**
	 * Creates a new attribute serialization with the given name and constant
	 * value
	 * @param name The name of the serialization
	 * @param constant The constant
	 * @return The corresponding serialization
	 */
	public static NameSerialization createConstantAttribute(String name, Object constant) {
		ConstantSerialization cons = new ConstantSerialization();
		cons.setConstant(constant);
		cons.setDelegate(new AttributeSerialization());
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(cons);
		return ser;
	}

	/**
	 * Creates a new value serialization with the given name
	 * @param name The name of the serialization
	 * @return The corresponding serialization
	 */
	public static NameSerialization createValue(String name) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(new ValueSerialization());
		return ser;
	}

	/**
	 * Creates a new value serialization resolving its value from the given
	 * property
	 * @param property The property
	 * @return The corresponding serialization
	 */
	public static PropertySerialization createPropertyValue(String property) {
		PropertySerialization ser = new PropertySerialization();
		ser.setProperty(property);
		ser.setDelegate(new ValueSerialization());
		return ser;
	}

	/**
	 * Creates a new value serialization with the given name and resolving
	 * it value from the given property
	 * @param name The name of the serialization
	 * @param property The property
	 * @return The corresponding serialization
	 */
	public static NameSerialization createValue(String name, String property) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(createPropertyValue(property));
		return ser;
	}

	/**
	 * Creates a new value serialization with the given name and constant
	 * value
	 * @param name The name of the serialization
	 * @param constant The constant
	 * @return The corresponding serialization
	 */
	public static NameSerialization createConstantValue(String name, Object constant) {
		ConstantSerialization cons = new ConstantSerialization();
		cons.setConstant(constant);
		cons.setDelegate(new ValueSerialization());
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(cons);
		return ser;
	}

	/**
	 * Creates a new complex serialization with the given name and attributes
	 * @param name The name of the serialization
	 * @param attributes The attributes to add
	 * @return The corresponding serialization
	 */
	public static NameSerialization createComplexWithAttributes(String name, Serialization...attributes) {
		ComplexSerialization complex = new ComplexSerialization();
		for (Serialization attribute : attributes) {
			complex.addAttribute(attribute);
		}
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(complex);
		return ser;
	}

	/**
	 * Creates a new complex serialization with the given name and elements
	 * @param name The name of the serialization
	 * @param elements The elements to add
	 * @return The corresponding serialization
	 */
	public static NameSerialization createComplexWithElements(String name, Serialization...elements) {
		ComplexSerialization complex = new ComplexSerialization();
		for (Serialization element : elements) {
			complex.addElement(element);
		}
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(complex);
		return ser;
	}

	/**
	 * Creates a new complex serialization with the given name, attributes and
	 * elements
	 * @param name The name of the serialization
	 * @param attributes The attributes to add
	 * @param elements The elements to add
	 * @return The corresponding serialization
	 */
	public static NameSerialization createComplex(String name, List<Serialization> attributes, List<Serialization> elements) {
		ComplexSerialization complex = new ComplexSerialization();
		for (Serialization attribute : attributes) {
			complex.addAttribute(attribute);
		}
		for (Serialization element : elements) {
			complex.addElement(element);
		}
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(complex);
		return ser;
	}

	/**
	 * Creates a new reference serialization with the given referenced 
	 * @param referenced The referenced serialization
	 * @return The corresponding serialization
	 */
	public static ReferenceSerialization createReference(Serialization referenced) {
		ReferenceSerialization serialization = new ReferenceSerialization();
		serialization.setReferenced(referenced);
		return serialization;
	}
	
}
