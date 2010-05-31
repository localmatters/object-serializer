package com.localmatters.serializer.serialization;

/**
 * Abstract class defining the common properties and functionalities of a 
 * serialization.
 */
public abstract class AbstractSerialization implements Serialization {
	private Boolean writeEmpty;
	
	/**
	 * @see com.localmatters.serializer.serialization.Serialization#isWriteEmpty()
	 */
	public boolean isWriteEmpty() {
		if (writeEmpty == null) {
			return false;
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
