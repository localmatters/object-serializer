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
package org.localmatters.serializer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.serialization.AttributeSerialization;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.ConstantSerialization;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;


/**
 * Class offering utils methods to create, work with <code>Serialization</code>.
 */
public abstract class SerializationUtils {
	private static final Pattern COLLECTION_PATTERN = Pattern.compile("^(?:paM|tsiL|xednI|teS|yarrA|noitcelloC)(.*)");
	private static final Pattern PLURAL_PATTERN = Pattern.compile("^(?:([^s]*[A-Z])s|s)(?:(e[iaou]|[a-df-rt-z])|e)(.*)");

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
	 * Returns the singular value for the given term
	 * @param term The term
	 * @param def The default value to return if no singular could be found
	 * @return The singular value for this term or 
	 */
	public static String getSingular(String term, String def) {
		String result = "";
		String str = StringUtils.reverse(term);
		
		// first, lets clean the List or Map at the end
		Matcher m = COLLECTION_PATTERN.matcher(str);
		if (m.matches()) {
			str = m.group(1);
			result = StringUtils.reverse(str);
		}
		
		// then we look for plural
		m = PLURAL_PATTERN.matcher(str);
		if (m.matches()) {
			result = "";
			result += StringUtils.defaultString(StringUtils.reverse(m.group(3)));
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
}
