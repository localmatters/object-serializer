package com.localmatters.serializer.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

import com.localmatters.serializer.serialization.AbstractSerialization;
import com.localmatters.serializer.serialization.AttributeSerialization;
import com.localmatters.serializer.serialization.BeanSerialization;
import com.localmatters.serializer.serialization.ComplexSerialization;
import com.localmatters.serializer.serialization.ConstantSerialization;
import com.localmatters.serializer.serialization.IteratorSerialization;
import com.localmatters.serializer.serialization.MapSerialization;
import com.localmatters.serializer.serialization.PropertySerialization;
import com.localmatters.serializer.serialization.ReferenceSerialization;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.serializer.serialization.ValueSerialization;
import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;
import com.localmatters.util.objectfactory.LMObjectFactory;

/**
 * The DOM4J element handler to parse the configuration elements
 */
public class SerializationElementHandler implements ElementHandler {
	protected static final String ATTRIBUTE_DISPLAY_EMPTY = "display-empty";
	protected static final String ATTRIBUTE_NAME = "name";
	protected static final String ATTRIBUTE_PROPERTY = "property";
	protected static final String ATTRIBUTE_BEAN = "bean";
	protected static final String ATTRIBUTE_CONSTANT = "constant";
	protected static final String ATTRIBUTE_KEY = "key";
	protected static final String ATTRIBUTE_ID = "id";
	protected static final String ATTRIBUTE_TARGET = "target";
	protected static final String ATTRIBUTE_PARENT = "parent";
	protected static final String TYPE_ATTRIBUTE = "attribute";
	protected static final String TYPE_MAP = "map";
	protected static final String TYPE_LIST = "list";
	protected static final String TYPE_VALUE = "value";
	protected static final String TYPE_COMPLEX = "complex";
	protected static final String TYPE_REFERENCE = "ref";
	protected static final String TYPE_COMMENT = "comment";
	protected static final String DUPLICATE_ID_FORMAT = "Duplicate elements found with the same id (\"%s\")!";
	protected static final String MISSING_ID = "All elements directly under the root must have a non-blank id!";
	protected static final String INVALID_TYPE_FORMAT = "Invalid element <%s> at [%s]!";
	protected static final String MISSING_ATTRIBUTE_FORMAT = "Missing or invalid attribute %s on [%s]!";
	protected static final String MISSING_NAME_OR_PARENT_FORMAT = "Complex element at [%s] requires either the name or the parrent attribute!";
	protected static final String INVALID_ATTRIBUTE_ELEMENT_FORMAT = "Unexpected <attribute> at [%s]. Attributes are only valid directly under <complex> elements!";
	protected static final String INVALID_LIST_FORMAT = "The list at [%s] is invalid. Only a single sub-element is expected!";
	protected static final String INVALID_MAP_FORMAT = "The map at [%s] is invalid. Only a single sub-element is expected!";
	protected static final String INVALID_ID_FORMAT = "Unable to find any element with the id(s): %s!";
	protected static final String INVALID_ATTRIBUTES_FORMAT = "Invalid attributes %s on element <%s> at [%s]";
	protected static final String INVALID_LOOP_REFERENCES = "The configuration defines a loop of parents; which is not allowed!";
	
	private LMObjectFactory objectFactory;
	private Map<String, Serialization> serializations = new HashMap<String, Serialization>();
	private Map<ReferenceSerialization, String> references = new HashMap<ReferenceSerialization, String>();
	private Map<String, ComplexSerialization> complexWithIds = new HashMap<String, ComplexSerialization>();
	private Map<ComplexSerialization, String> extensions = new HashMap<ComplexSerialization, String>();

	/**
	 * Constructor with the specification of the object factory
	 * @param objectFactory
	 */
	public SerializationElementHandler(LMObjectFactory objectFactory) {
		setObjectFactory(objectFactory);
	}
	
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
				reference.getKey().setReferenced(referenced);
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
		int size = CollectionUtils.sizeOf(extensions);
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
					serialization.setAttributes(CollectionUtils.mergeAsList(parent.getAttributes(), serialization.getAttributes()));
					serialization.setElements(CollectionUtils.mergeAsList(parent.getElements(), serialization.getElements()));
					if (StringUtils.isBlank(serialization.getName())) {
						serialization.setName(parent.getName());
					}
					if (serialization.getWriteEmpty() == null) {
						serialization.setWriteEmpty(parent.isWriteEmpty());
					}
				}
			}
			int newSize = CollectionUtils.sizeOf(extensions);
			if ((newSize != 0) && (newSize == size)) {
				throw new ConfigurationException(INVALID_LOOP_REFERENCES);
			}
			size = newSize;
		}
		return invalids;
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
			Serialization serialization = handleConstant(element, attributes);
			serializations.put(id, serialization);
			return serialization;
		} 
		if (required) {
			throw new ConfigurationException(MISSING_ID);
		}
		return handleConstant(element, attributes);
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
			ConstantSerialization serialization = getObjectFactory().create(ConstantSerialization.class);
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
			BeanSerialization serialization = getObjectFactory().create(BeanSerialization.class);
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
			PropertySerialization serialization = getObjectFactory().create(PropertySerialization.class);
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
		String name = element.attributeValue(ATTRIBUTE_NAME);
		String type = element.getName();

		if (TYPE_REFERENCE.equalsIgnoreCase(type)) {
			serialization = handleReference(element, attributes);
		} else if (TYPE_COMPLEX.equalsIgnoreCase(type)) {
			String parent = element.attributeValue(ATTRIBUTE_PARENT);
			if (StringUtils.isNotBlank(parent)) {
				attributes.put(ATTRIBUTE_PARENT, parent);
			} else if (StringUtils.isBlank(name)) {
				throw new ConfigurationException(MISSING_NAME_OR_PARENT_FORMAT, element.getPath());
			}
			serialization = handleComplex(element, attributes);
		} else {
			// the name is required for every type except the reference, comment
			// or complex with parent that will inherit it if it is not set
			if (StringUtils.isBlank(name)) {
				throw new ConfigurationException(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_NAME, element.getPath());
			}
			if (TYPE_ATTRIBUTE.equalsIgnoreCase(type)) {
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
		}
		
		if (StringUtils.isNotBlank(name)) {
			attributes.put(ATTRIBUTE_NAME, name);
			serialization.setName(name);
		}
		String displayEmpty = element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY);
		if (StringUtils.isNotBlank(displayEmpty)) {
			attributes.put(ATTRIBUTE_DISPLAY_EMPTY, displayEmpty);
			serialization.setWriteEmpty(Boolean.valueOf(displayEmpty));
		}

		// validates the number of attributes with the ones that have been
		// consumed to see if the element contains invalid attributes
		if (CollectionUtils.sizeOf(element.attributes()) != CollectionUtils.sizeOf(attributes)) {
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
		ComplexSerialization serialization = getObjectFactory().create(ComplexSerialization.class);

		List<Element> children = element.elements();
		if (CollectionUtils.isNotEmpty(children)) {
			for (Element child : children) {
				String type = child.getName();
				if (TYPE_COMMENT.equalsIgnoreCase(type)) {
					serialization.addComment(child.getStringValue());
				} else {
					Serialization childSerialization = handleId(child);
					if (TYPE_ATTRIBUTE.equalsIgnoreCase(type)) {
						serialization.addAttribute(childSerialization);
					} else {
						serialization.addElement(childSerialization);
				}
				}
			}
		}
		
		
		if (attributes.containsKey(ATTRIBUTE_ID)) {
			getComplexWithIds().put(attributes.get(ATTRIBUTE_ID), serialization);
		}
		if (attributes.containsKey(ATTRIBUTE_PARENT)) {
			getExtensions().put(serialization, attributes.get(ATTRIBUTE_PARENT));
		}
		
		return serialization;
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
			return getObjectFactory().create(AttributeSerialization.class);
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
		return getObjectFactory().create(ValueSerialization.class);
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

		ReferenceSerialization serialization = getObjectFactory().create(ReferenceSerialization.class);
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
		IteratorSerialization serialization = getObjectFactory().create(IteratorSerialization.class);
		
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
					Serialization elem = handleId(children.get(0));
					serialization.setElement(elem);
				}
			}
		}

		if (!foundElement) {
			throw new ConfigurationException(INVALID_LIST_FORMAT, element.getPath());
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
		MapSerialization serialization = getObjectFactory().create(MapSerialization.class);
		
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
					foundElement = true;
					Serialization elem = handleId(children.get(0));
					serialization.setValue(elem);
				}
			}
		}

		if (!foundElement) {
			throw new ConfigurationException(INVALID_MAP_FORMAT, element.getPath());
		}

		Serialization key = getObjectFactory().create(ValueSerialization.class);
		String property = element.attributeValue(ATTRIBUTE_KEY);
		if (StringUtils.isNotBlank(property)) {
			attributes.put(ATTRIBUTE_KEY, property);
			PropertySerialization propertyConfig = getObjectFactory().create(PropertySerialization.class);
			propertyConfig.setProperty(property);
			propertyConfig.setDelegate(key);
			key = propertyConfig;
		}

		serialization.setKey(key);
		return serialization;
	}

	/**
	 * @return The map of serialization
	 */
	public Map<String, Serialization> getSerializations() {
		return serializations;
	}

	/**
	 * @return The object factory
	 */
	private LMObjectFactory getObjectFactory() {
		return objectFactory;
	}

	/**
	 * @param objectFactory The object factory
	 */
	private void setObjectFactory(LMObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
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
