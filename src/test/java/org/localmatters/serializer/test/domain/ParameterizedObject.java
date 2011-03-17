/**
 * 
 */
package org.localmatters.serializer.test.domain;

import java.util.ArrayList;

/**
 * A test object that extends a generic type
 */
public class ParameterizedObject extends ArrayList<String> {
	private static final long serialVersionUID = 1L;
	private String name;
	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
