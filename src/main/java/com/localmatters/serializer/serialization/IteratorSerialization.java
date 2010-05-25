package com.localmatters.serializer.serialization;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * This class handles the serialization of an object that can be iterated over;
 * i.e. an Iterable, an Array or, even, a Map (through its Set of Map.Entry).
 */
public class IteratorSerialization extends AbstractSerialization {
	protected static final Iterator<?> EMTPY_ITERATOR = Collections.EMPTY_LIST.iterator();
	private Serialization elementSerialization;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
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
			throw new IteratorExpectedException(context.appendSegment(getName()));
		}
		
		return context.getSerializer().writeIterator(this, getElementSerialization(), itr, context.appendSegment(getName()));
	}

	/**
	 * @return The index elements serialization
	 */
	public Serialization getElementSerialization() {
		return elementSerialization;
	}

	/**
	 * @param elementSerialization The index elements serialization
	 */
	public void setElementSerialization(Serialization elementSerialization) {
		this.elementSerialization = elementSerialization;
	}
}
