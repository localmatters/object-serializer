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
package org.localmatters.serializer.serialization;

/**
 * Abstract class defining the common properties and functionalities of a 
 * serialization.
 */
public abstract class AbstractSerialization implements Serialization {
	private Boolean writeEmpty;
	
	/**
	 * @see org.localmatters.serializer.serialization.Serialization#isWriteEmpty()
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

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#getContextlessSerialization()
	 */
	public Serialization getContextlessSerialization() {
		return this;
	}

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#removeDefaultName()
	 */
	public String removeDefaultName() {
	    return null;
	}
}
