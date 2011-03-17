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
 * Exception raised when the serialization IO failed
 */
public class IOSerializationException extends SerializationException {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_FORMAT = "Exception encountered when serializing %s";

	/**
	 * Constructor with the specification of the context and cause exception
	 * @param property The property that could not be resolved
	 * @param cause The cause for this exception
	 */
	public IOSerializationException(SerializationContext context, Throwable cause) {
		super(String.format(MESSAGE_FORMAT, context.getPath()), cause);
	}
}
