package com.localmatters.serializer.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;


/**
 * Class describing the serialization configuration of an index (list, array, 
 * etc. including the support for maps; i.e. set of Map.Entry)
 */
public class IndexConfig extends AbstractConfig {
	protected static final Iterator<?> EMTPY_ITERATOR = Collections.EMPTY_LIST.iterator();
	private Config elementConfig;

	/**
	 * @see com.localmatters.serializer.config.Config#handle(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String handle(Object obj, SerializationContext context) throws SerializationException {
		Iterator<?> itr = null;
		
		// get an iterator over the array, iterable or map's entries
		if (obj == null) {
			itr = EMTPY_ITERATOR;
		} else if (obj.getClass().isArray()) {
			itr = Arrays.asList(obj).iterator();
		} else if (obj instanceof Iterable<?>) {
			itr = ((Iterable<?>) obj).iterator();
		} else if (obj instanceof Map<?, ?>) {
			itr = ((Map<?, ?>) obj).entrySet().iterator();
		} else {
			// if the object is not an array or an iterable, there is a
			// configuration problem
			throw new IndexExpectedException(context.appendSegment(getName()));
		}
		
		return context.getSerializer().serializeIndex(this, getElementConfig(), itr, context.appendSegment(getName()));
	}

	/**
	 * @return The configuration of the index element
	 */
	public Config getElementConfig() {
		return elementConfig;
	}

	/**
	 * @param elementConfig The configuration of the index element
	 */
	public void setElementConfig(Config elementConfig) {
		this.elementConfig = elementConfig;
	}
}
