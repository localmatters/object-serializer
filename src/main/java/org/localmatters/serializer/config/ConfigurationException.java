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
package org.localmatters.serializer.config;

/**
 * Describes an exception that is due to an error in the configuration
 */
public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with the specification of the exception message
	 * @param message The exception message
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor with the specification of the exception message format
	 * and its arguments
	 * @param format The exception message format
	 * @param args The message arguments
	 */
	public ConfigurationException(String format, Object...args) {
		super(String.format(format, args));
	}

	/**
	 * Constructor with the specification of the exception message and cause
	 * @param message The exception message
	 * @param cause The exception cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
