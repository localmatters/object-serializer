package com.localmatters.serializer.config;



/**
 * Class describing the serialization configuration of an object
 */
public abstract class AbstractConfig implements Config {
	private String name;
	private Boolean writeEmpty;

	/**
	 * @see com.localmatters.serializer.config.Config#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name under which the object will be serialized
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see com.localmatters.serializer.config.Config#isWriteEmpty()
	 */
	public boolean isWriteEmpty() {
		if (writeEmpty == null) {
			return true;
		}
		return writeEmpty;
	}
	
	/**
	 * @return Whether a null or empty object should be written
	 */
	public Boolean getWriteEmpty() {
		return writeEmpty;
	}

	/**
	 * @param writeEmpty Whether a null or empty object should be written
	 */
	public void setWriteEmpty(Boolean writeEmpty) {
		this.writeEmpty = writeEmpty;
	}
}
