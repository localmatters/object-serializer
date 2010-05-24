package com.localmatters.serializer;

/**
 * A dummy Object used for testing the formatting
 */
public class DummyObject {
	private String id;
	private String name;
	
	/**
	 * @return The id
	 */
	protected String getId() {
		return id;
	}

	/**
	 * @param id The id to set
	 */
	protected void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * @param name The name to set
	 */
	protected void setName(String name) {
		this.name = name;
	}
}
