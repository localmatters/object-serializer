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
package org.localmatters.serializer.resolver;



/**
 * This class defines the abstraction of a delegating resolver.
 */
public abstract class DelegatingPropertyResolver implements PropertyResolver {
	private PropertyResolver delegate;

	/**
	 * @return The delegate resolver
	 */
	public PropertyResolver getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate The delegate resolver
	 */
	public void setDelegate(PropertyResolver delegate) {
		this.delegate = delegate;
	}
}
