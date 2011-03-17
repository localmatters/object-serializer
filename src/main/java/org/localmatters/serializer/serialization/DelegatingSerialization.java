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
 * Abstraction of a serialization that delegates the object serialization to a
 * delegate. This enables adding some additional function to a serialization
 * without having to override, modify it. 
 */
public abstract class DelegatingSerialization implements Serialization {
	private Serialization delegate;
	
	/**
	 * @see org.localmatters.serializer.serialization.Serialization#isWriteEmpty()
	 */
	public boolean isWriteEmpty() {
		return getDelegate().isWriteEmpty();
	}
	
	/**
	 * @return The delegate configuration
	 */
	public Serialization getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate The delegate configuration
	 */
	public void setDelegate(Serialization delegate) {
		this.delegate = delegate;
	}

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#getContextlessSerialization()
	 */
	public Serialization getContextlessSerialization() {
		return getDelegate().getContextlessSerialization();
	}

	/**
	 * @see org.localmatters.serializer.serialization.Serialization#removeDefaultName()
	 */
	public String removeDefaultName() {
	    return getDelegate().removeDefaultName();
	}
}
