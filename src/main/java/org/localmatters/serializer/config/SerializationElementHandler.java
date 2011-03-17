package org.localmatters.serializer.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.localmatters.serializer.serialization.AbstractSerialization;
import org.localmatters.serializer.serialization.AttributeSerialization;
import org.localmatters.serializer.serialization.BeanSerialization;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.ConstantSerialization;
import org.localmatters.serializer.serialization.IteratorSerialization;
import org.localmatters.serializer.serialization.MapSerialization;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.PropertySerialization;
import org.localmatters.serializer.serialization.ReferenceSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.serialization.ValueSerialization;


/**
 * The DOM4J element handler to parse the configuration elements
 */
public class SerializationElementHandler implements ElementHandler {
	public static final String TYPE_ROOT = "serializations";
	public static final String ATTRIBUTE_DISPLAY_EMPTY = "display-empty";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_PROPERTY = "property";
	public static final String ATTRIBUTE_BEAN = "bean";
	public static final String ATTRIBUTE_CONSTANT = "constant";
	public static final String ATTRIBUTE_KEY = "key";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_TARGET = "target";
	public static final String ATTRIBUTE_PARENT = "parent";
	public static final String TYPE_ATTRIBUTE = "attribute";
	public static final String TYPE_MAP = "map";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_VALUE = "value";
	public static final String TYPE_COMPLEX = "complex";
	public static final String TYPE_REFERENCE = "ref";
	public static final String TYPE_COMMENT = "comment";
	public static final String DUPLICATE_ID_FORMAT = "Duplicate elements found with the same id (\"%s\")!";
	public static final String MISSING_ID = "All elements directly under the root must have a non-blank id!";
	public static final String INVALID_TYPE_FORMAT = "Invalid element <%s> at [%s]!";
	public static final String MISSING_ATTRIBUTE_FORMAT = "Missing or invalid attribute %s on [%s]!";
	public static final String INVALID_ATTRIBUTE_ELEMENT_FORMAT = "Unexpected <attribute> at [%s]. Attributes are only valid directly under <complex> elements!";
	public static final String INVALID_LIST_FORMAT = "The list at [%s] is invalid. Zero or one one non-comment sub-element expected!";
	public static final String INVALID_MAP_FORMAT = "The map at [%s] is invalid. Zero or one non-comment sub-element expected!";
	public static final String NAME_NOT_ALLOWED_FORMAT = "Name attribute are not allowed for the map sub-element at [%s]!";
	public static final String INVALID_ID_FORMAT = "Unable to find any element with the id(s): %s!";
	public static final String INVALID_ATTRIBUTES_FORMAT = "Invalid attributes %s on element <%s> at [%s]";
	public static final String INVALID_LOOP_REFERENCES = "The configuration defines a loop of parents; which is not allowed!";
	
	private Map<String, Serialization> serializations = new HashMap<String, Serialization>();
	private Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
	private Map<String, ComplexSerialization> complexWithIds = new HashMap<String, ComplexSerialization>();
	private Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>();

    /**
	 * @see org.dom4j.ElementHandler#onStart(org.dom4j.ElementPath)
	 */
	public void onStart(ElementPath elementPath) {
	}

	/**
	 * @see org.dom4j.ElementHandler#onEnd(org.dom4j.ElementPath)
	 */
	@SuppressWarnings("unchecked")
	public void onEnd(ElementPath elementPath) {
		Element root = elementPath.getCurrent();
		List<Element> elements = root.elements();
		for (Element element : elements) {
			handleId(element, new HashMap<String, String>(), true);
		}
		
		Set<String> invalids = resolveReferences(getReferences(), getSerializations());
		invalids.addAll(resolveExtensions(getExtensions(), getComplexWithIds()));
		
		if (CollectionUtils.isNotEmpty(invalids)) {
			throw new ConfigurationException(INVALID_ID_FORMAT, invalids);
		}
	}

	/**
	 * Resolves the references
	 * @param references The map of references
	 * @param configs The map of configuration
	 * @return The set of invalid references, if any
	 */
	protected static Set<String> resolveReferences(Map<ReferenceSerialization, String> references, Map<String, Serialization> configs) {
		Set<String> invalids = new HashSet<String>();
		for (Map.Entry<ReferenceSerialization, String> reference : references.entrySet()) {
			String target = reference.getValue();
			Serialization referenced = configs.get(target);
			if (referenced != null) {
				reference.getKey().setReferenced(referenced.getContextlessSerialization());
			} else {
				invalids.add(target);
			}
		}
		return invalids;
	}
	
	/**
	 * Resolves the extensions
	 * @param extensions The map of requested extensions
	 * @param parents The map of potential parents
	 * @return The set of invalid references, if any
	 */
	@SuppressWarnings("unchecked")
	protected static Set<String> resolveExtensions(Map<ComplexSerialization, String> extensions, Map<String, ComplexSerialization> parents) {
		Set<String> invalids = new HashSet<String>();
		int size = CollectionUtils.size(extensions);
		while (size != 0) {
			Iterator<Map.Entry<ComplexSerialization, String>> itr = extensions.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<ComplexSerialization, String> extension = itr.next();
				String id = extension.getValue();
				ComplexSerialization parent = parents.get(id);
				if (parent == null) {
					itr.remove();
					invalids.add(id);
				} else if (!extensions.containsKey(parent)) {
					itr.remove();
					ComplexSerialization serialization = extension.getKey();
					serialization.setAttributes(mergeAsList(parent.getAttributes(), serialization.getAttributes()));
					serialization.setElements(mergeAsList(parent.getElements(), serialization.getElements()));
					if (serialization.getWriteEmpty() == null) {
						serialization.setWriteEmpty(parent.isWriteEmpty());
					}
				}
			}
			int newSize = CollectionUtils.size(extensions);
			if ((newSize != 0) && (newSize == size)) {
				throw new ConfigurationException(INVALID_LOOP_REFERENCES);
			}
			size = newSize;
		}
		return invalids;
	}

	/**
	 * Merges the provided list together
	 * @param <T> The type of the list to merge
	 * @param lists The list to merge
	 * @return The result of the merge
	 */
    private static <T> List<T> mergeAsList(Collection<T> ... lists) {
        List<T> destList = new ArrayList<T>();
        for (Collection<T> list : lists) {
            if (list != null) {
                destList.addAll(list);
            }
        }
        return destList;
    }

	
	
	/**
	 * Handles the id attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @param required Whether the id is required or not
	 * @return The serialization for this element
	 */
	protected Serialization handleId(Element element, Map<String, String> attributes, boolean required) {
		String id = element.attributeValue(ATTRIBUTE_ID);
		if (StringUtils.isNotBlank(id)) {
			if (serializations.containsKey(id)) {
				throw new ConfigurationException(DUPLICATE_ID_FORMAT, id);
			}
			attributes.put(ATTRIBUTE_ID, id);
			Serialization serialization = handleName(element, attributes);
			serializations.put(id, serialization);
			return serialization;
		} 
		if (required) {
			throw new ConfigurationException(MISSING_ID);
		}
		return handleName(element, attributes);
	}

	/**
	 * Handles the optional id attribute
	 * @param element The element
	 * @return The serialization for this element
	 */
	protected Serialization handleId(Element element) {
		return handleId(element, new HashMap<String, String>(), false);
	}

	/**
	 * Handles the optional name attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected Serialization handleName(Element element, Map<String, String> attributes) {
		String name = element.attributeValue(ATTRIBUTE_NAME);
		if (StringUtils.isNotBlank(name)) {
			attributes.put(ATTRIBUTE_NAME, name);
			Serialization delegate = handleConstant(element, attributes);
			NameSerialization serialization = new NameSerialization();
			serialization.setName(name);
			serialization.setDelegate(delegate);
			return serialization;
		} 
		return handleConstant(element, attributes);
	}

	/**
	 * Handles the optional constant attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected Serialization handleConstant(Element element, Map<String, String> attributes) {
		String constant = element.attributeValue(ATTRIBUTE_CONSTANT);
		if (StringUtils.isNotBlank(constant)) {
			attributes.put(ATTRIBUTE_CONSTANT, constant);
			Serialization delegate = handleBean(element, attributes);
			ConstantSerialization serialization = new ConstantSerialization();
			serialization.setConstant(constant);
			serialization.setDelegate(delegate);
			return serialization;
		} 
		return handleBean(element, attributes);
	}

	/**
	 * Handles the optional bean attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected Serialization handleBean(Element element, Map<String, String> attributes) {
		String bean = element.attributeValue(ATTRIBUTE_BEAN);
		if (StringUtils.isNotBlank(bean)) {
			attributes.put(ATTRIBUTE_BEAN, bean);
			Serialization delegate = handleProperty(element, attributes);
			BeanSerialization serialization = new BeanSerialization();
			serialization.setBean(bean);
			serialization.setDelegate(delegate);
			return serialization;
		} 
		return handleProperty(element, attributes);
	}

	/**
	 * Handles the optional property attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected Serialization handleProperty(Element element, Map<String, String> attributes) {
		String property = element.attributeValue(ATTRIBUTE_PROPERTY);
		if (StringUtils.isNotBlank(property)) {
			attributes.put(ATTRIBUTE_PROPERTY, property);
			Serialization delegate = handleType(element, attributes);
			PropertySerialization serialization = new PropertySerialization();
			serialization.setProperty(property);
			serialization.setDelegate(delegate);
			return serialization;
		} 
		return handleType(element, attributes);
	}
	
	/**
	 * Handles the type of the element
	 * which is a special case)
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	@SuppressWarnings("unchecked")
	protected Serialization handleType(Element element, Map<String, String> attributes) {
		AbstractSerialization serialization = null;
		String type = element.getName();

		if (TYPE_REFERENCE.equalsIgnoreCase(type)) {
			serialization = handleReference(element, attributes);
		} else if (TYPE_COMPLEX.equalsIgnoreCase(type)) {
			String parent = element.attributeValue(ATTRIBUTE_PARENT);
			if (StringUtils.isNotBlank(parent)) {
				attributes.put(ATTRIBUTE_PARENT, parent);
			}
			serialization = handleComplex(element, attributes);
		} else if (TYPE_ATTRIBUTE.equalsIgnoreCase(type)) {
			serialization = handleAttribute(element, attributes);
		} else if (TYPE_VALUE.equalsIgnoreCase(type)) {
			serialization = handleValue(element, attributes);
		} else if (TYPE_LIST.equalsIgnoreCase(type)) {
			serialization = handleList(element, attributes);
		} else if (TYPE_MAP.equalsIgnoreCase(type)) {
			serialization = handleMap(element, attributes);
		} else {
			throw new ConfigurationException(INVALID_TYPE_FORMAT, type, element.getPath());
		}
		
		String displayEmpty = element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY);
		if (StringUtils.isNotBlank(displayEmpty)) {
			attributes.put(ATTRIBUTE_DISPLAY_EMPTY, displayEmpty);
			serialization.setWriteEmpty(Boolean.valueOf(displayEmpty));
		}

		// validates the number of attributes with the ones that have been
		// consumed to see if the element contains invalid attributes
		if (CollectionUtils.size(element.attributes()) != CollectionUtils.size(attributes)) {
			List<String> invalids = new ArrayList<String>();
			for (Element attribute : (List<Element>) element.attributes()) {
				String attributeName = attribute.getName();
				if (!attributes.containsKey(attributeName)) {
					invalids.add(attributeName);
				}
			}
			throw new ConfigurationException(INVALID_ATTRIBUTES_FORMAT, invalids, type, element.getPath());
		}
		return serialization;
	}
	
	/**
	 * Handles a complex element
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	@SuppressWarnings("unchecked")
	protected ComplexSerialization handleComplex(Element element, Map<String, String> attributes) {
		ComplexSerialization ser = new ComplexSerialization();

		List<Element> children = element.elements();
		if (CollectionUtils.isNotEmpty(children)) {
			for (Element child : children) {
				String type = child.getName();
				if (TYPE_COMMENT.equalsIgnoreCase(type)) {
					ser.addComment(child.getStringValue());
				} else {
					Serialization childSerialization = handleId(child);
					if (TYPE_ATTRIBUTE.equalsIgnoreCase(type)) {
						ser.addAttribute(childSerialization);
					} else {
						ser.addElement(childSerialization);
					}
				}
			}
		}
		
		if (attributes.containsKey(ATTRIBUTE_ID)) {
			getComplexWithIds().put(attributes.get(ATTRIBUTE_ID), ser);
		}
		if (attributes.containsKey(ATTRIBUTE_PARENT)) {
			getExtensions().put(ser, attributes.get(ATTRIBUTE_PARENT));
		}
		
		return ser;
	}

	/**
	 * Handles a complex element attribute (<code>&lt;attribute&gt;</code>).
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected AttributeSerialization handleAttribute(Element element, Map<String, String> attributes) {
		Element parent = element.getParent();
		if ((parent != null) && TYPE_COMPLEX.equalsIgnoreCase(parent.getName())) {
			return new AttributeSerialization();
		}
		throw new ConfigurationException(INVALID_ATTRIBUTE_ELEMENT_FORMAT, element.getPath());
	}

	/**
	 * Handles a value element
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected ValueSerialization handleValue(Element element, Map<String, String> attributes) {
		return new ValueSerialization();
	}

	/**
	 * Handles a reference
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	protected ReferenceSerialization handleReference(Element element, Map<String, String> attributes) {
		String target = element.attributeValue(ATTRIBUTE_TARGET);
		if (StringUtils.isBlank(target)) {
			throw new ConfigurationException(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_TARGET, element.getPath());
		}
		attributes.put(ATTRIBUTE_TARGET, target);

		ReferenceSerialization serialization = new ReferenceSerialization();
		getReferences().put(serialization, target);
		return serialization;
	}

	/**
	 * Handles a list
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	@SuppressWarnings("unchecked")
	protected IteratorSerialization handleList(Element element, Map<String, String> attributes) {
		IteratorSerialization serialization = new IteratorSerialization();
		
		boolean foundElement = false;
		List<Element> children = element.elements();
		if (CollectionUtils.isNotEmpty(children)) {
			for (Element child : children) {
				String type = child.getName();
				if (TYPE_COMMENT.equalsIgnoreCase(type)) {
					serialization.addComment(child.getStringValue());
				} else if (foundElement) {
					throw new ConfigurationException(INVALID_LIST_FORMAT, element.getPath());
				} else {
					foundElement = true;
					Serialization elem = handleId(child);
					serialization.setElement(elem);
				}
			}
		}

		if (!foundElement) {
			serialization.setElement(new ValueSerialization());
		}
		return serialization;
	}

	/**
	 * Handles a map
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The serialization for this element
	 */
	@SuppressWarnings("unchecked")
	protected MapSerialization handleMap(Element element, Map<String, String> attributes) {
		MapSerialization serialization = new MapSerialization();
		
		boolean foundElement = false;
		List<Element> children = element.elements();
		if (CollectionUtils.isNotEmpty(children)) {
			for (Element child : children) {
				String type = child.getName();
				if (TYPE_COMMENT.equalsIgnoreCase(type)) {
					serialization.addComment(child.getStringValue());
				} else if (foundElement) {
					throw new ConfigurationException(INVALID_MAP_FORMAT, element.getPath());
				} else {
					if (StringUtils.isNotBlank(child.attributeValue(ATTRIBUTE_NAME))) {
						throw new ConfigurationException(NAME_NOT_ALLOWED_FORMAT, element.getPath());
					}
					foundElement = true;
					Serialization elem = handleId(child);
					serialization.setValue(elem);
				}
			}
		}

		if (!foundElement) {
			serialization.setValue(new ValueSerialization());
		}

		String key = element.attributeValue(ATTRIBUTE_KEY);
		if (StringUtils.isNotBlank(key)) {
			serialization.setKey(key);
			attributes.put(ATTRIBUTE_KEY, key);
		}
		
		return serialization;
	}

	/**
	 * @return The map of serialization
	 */
	public Map<String, Serialization> getSerializations() {
		return serializations;
	}

    /**
	 * @return The map of references
	 */
	protected Map<ReferenceSerialization, String> getReferences() {
		return references;
	}

	/**
	 * @return The map of extensions
	 */
	protected Map<ComplexSerialization, String> getExtensions() {
		return extensions;
	}

	/**
	 * @return The map of complex bean with ID
	 */
	protected Map<String, ComplexSerialization> getComplexWithIds() {
		return complexWithIds;
	}
}
