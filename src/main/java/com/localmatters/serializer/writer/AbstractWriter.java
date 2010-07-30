package com.localmatters.serializer.writer;

import java.io.IOException;
import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;
import com.localmatters.serializer.resolver.PropertyResolverException;
import com.localmatters.serializer.serialization.IOSerializationException;
import com.localmatters.serializer.serialization.NameExpectedException;
import com.localmatters.serializer.serialization.UnknownPropertyException;
import com.localmatters.util.StringUtils;

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
				byte[] prefix = ctx.consomePrefix();
				if (prefix != null) {
					ctx.getOutputStream().write(prefix);
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
		key = StringUtils.replacePattern(StringUtils.trim(key), "([^\\w_-])", "-");
		key = StringUtils.replacePattern(key, "^(\\d)", "_$1");
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