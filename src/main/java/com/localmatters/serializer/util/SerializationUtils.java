/**
 * 
 */
package com.localmatters.serializer.util;

import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.NameSerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;

/**
 * Class offering utils methods to create, work with <code>Serialization</code>.
 */
public abstract class SerializationUtils {

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
	 * Creates a new complex serialization with the given attributes
	 * @param attributes The attributes to add
	 * @return The corresponding serialization
	 */
	public static ComplexSerialization createComplex(Serialization...attributes) {
		ComplexSerialization ser = new ComplexSerialization();
		for (Serialization attribute : attributes) {
			ser.addAttribute(attribute);
		}
		return ser;
	}

	/**
	 * Creates a new complex serialization with the given name and attributes
	 * @param name The name of the serialization
	 * @param attributes The attributes to add
	 * @return The corresponding serialization
	 */
	public static NameSerialization createComplex(String name, Serialization...attributes) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(createComplex(attributes));
		return ser;
	}

	/**
	 * Creates a new name serialization with the given name and delegate
	 * @param name The name of the serialization
	 * @return The corresponding serialization
	 */
	public static NameSerialization createName(String name, Serialization delegate) {
		NameSerialization ser = new NameSerialization();
		ser.setName(name);
		ser.setDelegate(delegate);
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
}
