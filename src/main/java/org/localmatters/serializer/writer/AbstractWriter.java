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
package org.localmatters.serializer.writer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.resolver.PropertyResolverException;
import org.localmatters.serializer.serialization.IOSerializationException;
import org.localmatters.serializer.serialization.NameExpectedException;
import org.localmatters.serializer.serialization.UnknownPropertyException;


/**
 * This class defines a common functionalities between serializers
 */
public abstract class AbstractWriter implements Writer {

	/**
	 * Writes the given string
	 * @param ctx The context
	 * @param str The string to write
	 * @return The writer itself for ease of coding
	 * @throws SerializationException When the writing failed
	 */
	protected AbstractWriter write(SerializationContext ctx, String str) throws SerializationException {
		return write(ctx, str.getBytes());
	}

	/**
	 * Writes the given byte array
	 * @param ctx The context
	 * @param bytes The byte array to write
	 * @return The writer itself for ease of coding
	 * @throws SerializationException When the writing failed
	 */
	protected AbstractWriter write(SerializationContext ctx, byte[] bytes) throws SerializationException {
		try {
			if (bytes.length > 0) {
				Iterator<byte[]> itr = ctx.getPrefixes().iterator();
				while (itr.hasNext()) {
				    byte[] prefix = itr.next();
				    ctx.getOutputStream().write(prefix);
				    itr.remove();
				}
				ctx.getOutputStream().write(bytes);
			}
		} catch (IOException e) {
			throw new IOSerializationException(ctx, e);
		}
		return this;
	}

	/**
	 * Resolves the serialization key for the given map entry 
	 * @param keyProperty The property of the map entry key that should be 
	 * serialized as the key
	 * @param entry The map entry
	 * @param ctx The serialization context
	 * @return The corresponding key
	 * @throws UnknownPropertyException When the key could not be found
	 */
	protected static String resolvesMapKey(String keyProperty, 
			Map.Entry<?, ?> entry,
			SerializationContext ctx)
			throws UnknownPropertyException {
		String key = null;
		if (StringUtils.isNotBlank(keyProperty)) {
			try {
				key = String.valueOf(ctx.getPropertyResolver().resolve(entry.getKey(), keyProperty));
			} catch (PropertyResolverException e) {
				throw new UnknownPropertyException(keyProperty, ctx, e);
			}
		} else {
			key = String.valueOf(entry.getKey());
		}
		key = StringUtils.defaultString(key).trim().replaceAll("([^\\w_-]+)", "-").replaceAll("^(\\d)", "_$1");
		return key;
	}
	
	/**
	 * Checks that the given name is not blank
	 * @param ctx The serialization context
	 * @param name The name to evaluate
	 * @return The name if it is not blank
	 * @throws NameExpectedException Thrown when the name is blank
	 */
	protected static String checkRequiredName(SerializationContext ctx, String name) throws NameExpectedException {
		if (StringUtils.isBlank(name)) {
			throw new NameExpectedException(ctx);
		}
		return name;
	}
}