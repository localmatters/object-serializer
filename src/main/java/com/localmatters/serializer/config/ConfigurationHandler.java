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

import com.localmatters.util.CollectionUtils;
import com.localmatters.util.StringUtils;
import com.localmatters.util.objectfactory.LMObjectFactory;

/**
 * The handler to parse the configuration elements
 */
public class ConfigurationHandler implements ElementHandler {
	protected static final String ATTRIBUTE_DISPLAY_EMPTY = "display-empty";
	protected static final String ATTRIBUTE_NAME = "name";
	protected static final String ATTRIBUTE_PROPERTY = "property";
	protected static final String ATTRIBUTE_BEAN = "bean";
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
	private Map<String, Config> configs = new HashMap<String, Config>();
	private Map<ReferenceConfig, String> references = new HashMap<ReferenceConfig, String>();
	private Map<String, ComplexConfig> complexWithIds = new HashMap<String, ComplexConfig>();
	private Map<ComplexConfig, String> extensions = new HashMap<ComplexConfig, String>();
	

	/**
	 * Constructor with the specification of the object factory
	 * @param objectFactory
	 */
	public ConfigurationHandler(LMObjectFactory objectFactory) {
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
		
		Set<String> invalids = resolveReferences(getReferences(), getConfigs());
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
	protected static Set<String> resolveReferences(Map<ReferenceConfig, String> references, Map<String, Config> configs) {
		Set<String> invalids = new HashSet<String>();
		for (Map.Entry<ReferenceConfig, String> reference : references.entrySet()) {
			String target = reference.getValue();
			Config delegate = configs.get(target);
			if (delegate != null) {
				reference.getKey().setDelegate(delegate);
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
	protected static Set<String> resolveExtensions(Map<ComplexConfig, String> extensions, Map<String, ComplexConfig> parents) {
		Set<String> invalids = new HashSet<String>();
		int size = CollectionUtils.sizeOf(extensions);
		while (size != 0) {
			Iterator<Map.Entry<ComplexConfig, String>> itr = extensions.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<ComplexConfig, String> extension = itr.next();
				String id = extension.getValue();
				ComplexConfig parent = parents.get(id);
				if (parent == null) {
					itr.remove();
					invalids.add(id);
				} else if (!extensions.containsKey(parent)) {
					itr.remove();
					ComplexConfig config = extension.getKey();
					config.setAttributeConfigs(CollectionUtils.mergeAsList(parent.getAttributeConfigs(), config.getAttributeConfigs()));
					config.setElementConfigs(CollectionUtils.mergeAsList(parent.getElementConfigs(), config.getElementConfigs()));
					if (StringUtils.isBlank(config.getName())) {
						config.setName(parent.getName());
					}
					if (config.getWriteEmpty() == null) {
						config.setWriteEmpty(parent.isWriteEmpty());
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
	 * Handles the optional id attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @param required Whether the id is required or not
	 * @return The configuration for this element
	 */
	protected Config handleId(Element element, Map<String, String> attributes, boolean required) {
		String id = element.attributeValue(ATTRIBUTE_ID);
		if (StringUtils.isNotBlank(id)) {
			if (configs.containsKey(id)) {
				throw new ConfigurationException(DUPLICATE_ID_FORMAT, id);
			}
			attributes.put(ATTRIBUTE_ID, id);
			Config config = handleBean(element, attributes);
			configs.put(id, config);
			return config;
		} 
		if (required) {
			throw new ConfigurationException(MISSING_ID);
		}
		return handleBean(element, attributes);
	}

	/**
	 * Handles the optional id attribute
	 * @param element The element
	 * @return The configuration for this element
	 */
	protected Config handleId(Element element) {
		return handleId(element, new HashMap<String, String>(), false);
	}

	/**
	 * Handles the optional bean attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	protected Config handleBean(Element element, Map<String, String> attributes) {
		String bean = element.attributeValue(ATTRIBUTE_BEAN);
		if (StringUtils.isNotBlank(bean)) {
			attributes.put(ATTRIBUTE_BEAN, bean);
			Config delegateConfig = handleProperty(element, attributes);
			BeanConfig config = getObjectFactory().create(BeanConfig.class);
			config.setBean(bean);
			config.setDelegate(delegateConfig);
			return config;
		} 
		return handleProperty(element, attributes);
	}

	/**
	 * Handles the optional property attribute
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	protected Config handleProperty(Element element, Map<String, String> attributes) {
		String property = element.attributeValue(ATTRIBUTE_PROPERTY);
		if (StringUtils.isNotBlank(property)) {
			attributes.put(ATTRIBUTE_PROPERTY, property);
			Config delegateConfig = handleType(element, attributes);
			PropertyConfig config = getObjectFactory().create(PropertyConfig.class);
			config.setProperty(property);
			config.setDelegate(delegateConfig);
			return config;
		} 
		return handleType(element, attributes);
	}
	
	/**
	 * Handles the type of the element
	 * which is a special case)
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	@SuppressWarnings("unchecked")
	protected Config handleType(Element element, Map<String, String> attributes) {
		AbstractConfig config = null;
		String name = element.attributeValue(ATTRIBUTE_NAME);
		String type = element.getName();

		if (TYPE_REFERENCE.equalsIgnoreCase(type)) {
			config = handleReference(element, attributes);
		} else if (TYPE_COMPLEX.equalsIgnoreCase(type)) {
			String parent = element.attributeValue(ATTRIBUTE_PARENT);
			if (StringUtils.isNotBlank(parent)) {
				attributes.put(ATTRIBUTE_PARENT, parent);
			} else if (StringUtils.isBlank(name)) {
				throw new ConfigurationException(MISSING_NAME_OR_PARENT_FORMAT, element.getPath());
			}
			config = handleComplex(element, attributes);
		} else {
			// the name is required for every type except the reference or 
			// complex with parent that will inherit it if it is not set
			if (StringUtils.isBlank(name)) {
				throw new ConfigurationException(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_NAME, element.getPath());
			}
			if (TYPE_ATTRIBUTE.equalsIgnoreCase(type)) {
				config = handleAttribute(element, attributes);
			} else if (TYPE_VALUE.equalsIgnoreCase(type)) {
				config = handleValue(element, attributes);
			} else if (TYPE_LIST.equalsIgnoreCase(type)) {
				config = handleList(element, attributes);
			} else if (TYPE_MAP.equalsIgnoreCase(type)) {
				config = handleMap(element, attributes);
			} else {
				throw new ConfigurationException(INVALID_TYPE_FORMAT, type, element.getPath());
			}
		}
		
		if (StringUtils.isNotBlank(name)) {
			attributes.put(ATTRIBUTE_NAME, name);
			config.setName(name);
		}
		String displayEmpty = element.attributeValue(ATTRIBUTE_DISPLAY_EMPTY);
		if (StringUtils.isNotBlank(displayEmpty)) {
			attributes.put(ATTRIBUTE_DISPLAY_EMPTY, displayEmpty);
			config.setWriteEmpty(Boolean.valueOf(displayEmpty));
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
		return config;
	}

	/**
	 * Handles a complex element
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	@SuppressWarnings("unchecked")
	protected ComplexConfig handleComplex(Element element, Map<String, String> attributes) {
		List<Config> attributeConfigs = new ArrayList<Config>();
		List<Config> elementConfigs = new ArrayList<Config>();
		
		List<Element> children = element.elements();
		if (CollectionUtils.isNotEmpty(children)) {
			for (Element child : children) {
				Config childConfig = handleId(child);
				if (TYPE_ATTRIBUTE.equalsIgnoreCase(child.getName())) {
					attributeConfigs.add(childConfig);
				} else {
					elementConfigs.add(childConfig);
				}
			}
		}
		
		ComplexConfig config = getObjectFactory().create(ComplexConfig.class);
		config.setAttributeConfigs(attributeConfigs);
		config.setElementConfigs(elementConfigs);
		
		if (attributes.containsKey(ATTRIBUTE_ID)) {
			getComplexWithIds().put(attributes.get(ATTRIBUTE_ID), config);
		}
		if (attributes.containsKey(ATTRIBUTE_PARENT)) {
			getExtensions().put(config, attributes.get(ATTRIBUTE_PARENT));
		}
		
		return config;
	}

	/**
	 * Handles a complex element attribute (<code>&lt;attribute&gt;</code>).
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	protected AttributeConfig handleAttribute(Element element, Map<String, String> attributes) {
		Element parent = element.getParent();
		if ((parent != null) && TYPE_COMPLEX.equalsIgnoreCase(parent.getName())) {
			return getObjectFactory().create(AttributeConfig.class);
		}
		throw new ConfigurationException(INVALID_ATTRIBUTE_ELEMENT_FORMAT, element.getPath());
	}

	/**
	 * Handles a value element
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	protected ValueConfig handleValue(Element element, Map<String, String> attributes) {
		return getObjectFactory().create(ValueConfig.class);
	}

	/**
	 * Handles a reference
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	protected ReferenceConfig handleReference(Element element, Map<String, String> attributes) {
		String target = element.attributeValue(ATTRIBUTE_TARGET);
		if (StringUtils.isBlank(target)) {
			throw new ConfigurationException(MISSING_ATTRIBUTE_FORMAT, ATTRIBUTE_TARGET, element.getPath());
		}
		attributes.put(ATTRIBUTE_TARGET, target);

		ReferenceConfig config = getObjectFactory().create(ReferenceConfig.class);
		getReferences().put(config, target);
		return config;
	}

	/**
	 * Handles a list
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	@SuppressWarnings("unchecked")
	protected IndexConfig handleList(Element element, Map<String, String> attributes) {
		List<Element> children = element.elements();
		if (CollectionUtils.sizeOf(children) != 1) {
			throw new ConfigurationException(INVALID_LIST_FORMAT, element.getPath());
		}
		Config elementConfig = handleId(children.get(0));
		IndexConfig config = getObjectFactory().create(IndexConfig.class);
		config.setElementConfig(elementConfig);
		return config;
	}

	/**
	 * Handles a map
	 * @param element The element
	 * @param attributes The map of attributes consumed for this element
	 * @return The configuration for this element
	 */
	@SuppressWarnings("unchecked")
	protected MapConfig handleMap(Element element, Map<String, String> attributes) {
		List<Element> children = element.elements();
		if (CollectionUtils.sizeOf(children) != 1) {
			throw new ConfigurationException(INVALID_MAP_FORMAT, element.getPath());
		}
		Config valueConfig = handleId(children.get(0));

		Config keyConfig = getObjectFactory().create(ValueConfig.class);
		String key = element.attributeValue(ATTRIBUTE_KEY);
		if (StringUtils.isNotBlank(key)) {
			attributes.put(ATTRIBUTE_KEY, key);
			PropertyConfig propertyConfig = getObjectFactory().create(PropertyConfig.class);
			propertyConfig.setProperty(key);
			propertyConfig.setDelegate(keyConfig);
			keyConfig = propertyConfig;
		}

		MapConfig config = getObjectFactory().create(MapConfig.class);
		config.setKeyConfig(keyConfig);
		config.setValueConfig(valueConfig);
		return config;
	}

	/**
	 * @return The map of configuration
	 */
	public Map<String, Config> getConfigs() {
		return configs;
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
	protected Map<ReferenceConfig, String> getReferences() {
		return references;
	}

	/**
	 * @return The map of extensions
	 */
	protected Map<ComplexConfig, String> getExtensions() {
		return extensions;
	}

	/**
	 * @return The map of complex bean with ID
	 */
	protected Map<String, ComplexConfig> getComplexWithIds() {
		return complexWithIds;
	}
}
