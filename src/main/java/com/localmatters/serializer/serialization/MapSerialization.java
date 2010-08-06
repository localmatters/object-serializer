package com.localmatters.serializer.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * This class handles the serialization of a simple map; i.e. a map for which 
 * the keys will be serialized simply as a value. The 
 * <code>IteratorSerialization</code> can be used to handle maps for which the 
 * keys should be serialized as more complex elements. Note that this class
 * can also serialize arrays and iterables as a map; in this case, both the
 * key and value are the same object.
 */
public class MapSerialization extends CommentSerialization {
	private String key;
	private Serialization value;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(com.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void serialize(Serialization ser, String name, Object obj, SerializationContext ctx) throws SerializationException {
		Collection<Map.Entry> entries = null;
		if (obj instanceof Map) {
			entries = ((Map) obj).entrySet();
		} else if (obj != null) {
			if (obj.getClass().isArray()) {
				entries = new ArrayList<Map.Entry>();
				for (Object entry : (Object[]) obj) {
					entries.add(new SameKeyValueEntry(entry));
				}
			} else if (obj instanceof Iterable) {
				entries = new ArrayList<Map.Entry>();
				for (Object entry : (Iterable) obj) {
					entries.add(new SameKeyValueEntry(entry));
				}
			} else {
				throw new MapExpectedException(ctx.nextLevel(name));
			}
		}
		ctx.getWriter().writeMap(ser, name, entries, getKey(), getValue(), getComments(), ctx);
	}

	/**
	 * @return The property to resolve the key or null if the key should be
	 * serialized "as it"
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key The property to resolve the key or null if the key should be
	 * serialized "as it"
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return The values serialization
	 */
	public Serialization getValue() {
		return value;
	}

	/**
	 * @param value The values serialization
	 */
	public void setValue(Serialization value) {
		this.value = value;
	}
	
	/**
	 * An implementation of the Map.Entry interface to transform arrays and
	 * iterables into a collection of Map.Entry where the key and value are the
	 * same object
	 * @param <E>
	 */
	protected static class SameKeyValueEntry<E> implements Map.Entry<E, E> {
		private E entry;

		/**
		 * Default constructor with the specification of the entry
		 * @param entry
		 */
		public SameKeyValueEntry(E entry) {
			setValue(entry);
		}
		
		/**
		 * @return The entry
		 */
		public E getEntry() {
			return entry;
		}

		/**
		 * @param entry The entry
		 */
		public void setEntry(E entry) {
			this.entry = entry;
		}

		/**
		 * @see java.util.Map.Entry#getKey()
		 */
		public E getKey() {
			return getEntry();
		}

		/**
		 * @see java.util.Map.Entry#getValue()
		 */
		public E getValue() {
			return getEntry();
		}

		/**
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		public E setValue(E value) {
			E old = getEntry();
			setEntry(value);
			return old;
		}
	}
}
