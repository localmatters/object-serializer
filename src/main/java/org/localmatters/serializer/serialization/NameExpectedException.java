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

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

/**
 * Exception raised when the serialization name is required, but missing
 */
public class NameExpectedException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "The serialization of %s requires a name, but none has been specified either by the name or property attribute in the config!";

	/**
	 * Constructor with the specification of the context
	 * @param context The serialization context
	 */
	public NameExpectedException(SerializationContext context) {
		super(String.format(MESSAGE_FORMAT, context.getPath()));
	}
}
