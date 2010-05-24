package com.localmatters.serializer;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.resolver.PropertyResolver;

/**
 * Provides the context for the serialization of a node 
 */
public class SerializationContext {
	private static final String SEPARATOR = ".";
	private static final String MAP = "{}";
	private static final String INDEX_FORMAT = "[%d]";
	private static final String MAP_FORMAT = "{%s}";
	private String path;
	private Serializer serializer;
	private PropertyResolver propertyResolver;
	private Map<String, Object> beans;

	/**
	 * Default constructor
	 * @param serializer The serializer
	 * @param beans The map of beans available to the serialization
	 * @param propertyResolver The resolver to resolve the property of an object
	 * @param path The path to this level
	 */
	public SerializationContext(Serializer serializer, Map<String, Object> beans, PropertyResolver propertyResolver) {
		this(serializer, beans, propertyResolver, StringUtils.EMPTY);
	}

	/**
	 * Constructor with the specification of the start path, serializer, beans
	 * map and property resolver
	 * @param serializer The serializer
	 * @param beans The map of beans available to the serialization
	 * @param propertyResolver The resolver to resolve the property of an object
	 * @param path The path to this level
	 */
	protected SerializationContext(Serializer serializer, Map<String, Object> beans, PropertyResolver propertyResolver, String path) {
		setSerializer(serializer);
		setBeans(beans);
		setPropertyResolver(propertyResolver);
		setPath(path);
	}
	
	/**
	 * Appends the given extension
	 * @param extension The extension to append
	 * @return The new path describing the path resulting from this path + the
	 * extension
	 */
	private SerializationContext append(String extension) {
		return new SerializationContext(getSerializer(), getBeans(), getPropertyResolver(), getPath() + extension);
	}
	
	/**
	 * Returns the path resulting from adding the given segment to this path.
	 * Note that this will not modify this instance.
	 * @param segment The segment to append 
	 * @return The Path instance with the new segment
	 */
	public SerializationContext appendSegment(String segment) {
		if (StringUtils.isNotBlank(getPath())) {
			return append(SEPARATOR + segment);
		}
		return new SerializationContext(getSerializer(), getBeans(), getPropertyResolver(), segment);
	}
	
	/**
	 * Returns the path resulting from adding the given index (of a list) to 
	 * this path. Note that this will not modify this instance.
	 * @param index The index to append 
	 * @return The Path instance with the new index
	 */
	public SerializationContext appendIndex(int index) {
		return append(String.format(INDEX_FORMAT, index));
	}
	
	/**
	 * Returns the path resulting from adding the given index (of a list), 
	 * followed by the given segment to this path. Note that this will not 
	 * modify this instance.
	 * @param index The index to append 
	 * @param segment The segment to append
	 * @return The Path instance with the new index and segment
	 */
	public SerializationContext appendIndexAndSegment(int index, String segment) {
		return append(String.format(INDEX_FORMAT, index) + SEPARATOR + segment);
	}
	
	/**
	 * Returns the path resulting from adding the map definition to this path. 
	 * Note that this will not modify this instance.
	 * @return The Path instance with the map definition
	 */
	public SerializationContext appendMap() {
		return append(MAP);
	}
	
	/**
	 * Returns the path resulting from adding the given map key to this path. 
	 * Note that this will not modify this instance.
	 * @param key The map key to append 
	 * @return The Path instance with the new map key
	 */
	public SerializationContext appendMap(String key) {
		return append(String.format(MAP_FORMAT, key));
	}
	
	/**
	 * @return The path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path The path to set
	 */
	private void setPath(String path) {
		this.path = path;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SerializationContext) {
			SerializationContext ctx = (SerializationContext) obj;
			
			// Note that with want the instance equality here for the serializer
			// and, mostly, for the beans as both context should reference the
			// same objects to be equal (and this is the way they are expected
			// tp be built.
			return getPath().equals(ctx.getPath()) && 
				(getSerializer() == ctx.getSerializer()) &&
				(getBeans() == ctx.getBeans()) &&
				(getPropertyResolver() == ctx.getPropertyResolver());
		}
		return super.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// see the equals method to see why this is intended
		return (getPath() + getSerializer() + getBeans() + getPropertyResolver()).hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPath();
	}
	
	/**
	 * @return The serializer
	 */
	public Serializer getSerializer() {
		return serializer;
	}
	
	/**
	 * @param serializer The serializer
	 */
	private void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
	
	/**
	 * @return The map of beans available to the serialization
	 */
	private Map<String, Object> getBeans() {
		return beans;
	}
	
	/**
	 * Returns the bean exposed under the specified name
	 * @param name The name of the bean to retrieve
	 * @return The corresponding bean or null
	 */
	public Object getBean(String name) {
		return beans.get(name);
	}
	
	/**
	 * @param beans The map of beans available to the serialization
	 */
	private void setBeans(Map<String, Object> beans) {
		this.beans = beans;
	}
	
	/**
	 * @return The resolver to resolve the property of an object
	 */
	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	/**
	 * @param propertyResolver The resolver to resolve the property of an object
	 */
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}
}
