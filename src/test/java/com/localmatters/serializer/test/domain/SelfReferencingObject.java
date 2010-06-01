/**
 * 
 */
package com.localmatters.serializer.test.domain;

import java.util.List;

/**
 * A sefl referencing class
 */
public class SelfReferencingObject {
	private String name;
	private List<SelfReferencingObject> others;
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
	/**
	 * @return The others
	 */
	public List<SelfReferencingObject> getOthers() {
		return others;
	}
	/**
	 * @param others The others to set
	 */
	public void setOthers(List<SelfReferencingObject> others) {
		this.others = others;
	}
}
