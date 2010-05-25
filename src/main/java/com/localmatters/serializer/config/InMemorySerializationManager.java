package com.localmatters.serializer.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.util.objectfactory.LMObjectFactory;
import com.localmatters.util.refreshable.AbstractRefreshableContent;

/**
 * A refreshable in memory serialization manager
 */
public class InMemorySerializationManager extends AbstractRefreshableContent implements SerializationManager {
	protected static final String DEFAULT_ENCODING = "UTF-8";
	private Resource resource;
	private String encoding = DEFAULT_ENCODING;
	private Map<String, Serialization> serializations = Collections.emptyMap();
	private LMObjectFactory objectFactory;
	
	/**
	 * @see com.localmatters.util.refreshable.AbstractRefreshableContent#doRefresh()
	 */
	@Override
	protected void doRefresh() throws Exception {
		Reader reader = new InputStreamReader(getResource().getInputStream(), getEncoding());
		SAXReader saxReader = new SAXReader();
		SerializationElementHandler handler = new SerializationElementHandler(getObjectFactory());
		saxReader.addHandler("/serializations", handler);
		try {
			saxReader.read(reader);
		} catch (DocumentException e) {
			if (e.getNestedException() instanceof ConfigurationException) {
				throw (ConfigurationException) e.getNestedException();
			}
			throw new ConfigurationException(String.format("Unable to load the handlers configuration %s!", getContentSource()), e);
		}
		serializations = handler.getConfigs();
	}

	/**
	 * @see com.localmatters.serializer.config.SerializationManager#getSerialization(java.lang.String)
	 */
	public Serialization getSerialization(String id) {
		return serializations.get(id);
	}


	/**
	 * @see com.localmatters.util.refreshable.AbstractRefreshableContent#getContentSource()
	 */
	@Override
	public String getContentSource() {
		return getResource().getDescription();
	}

	/**
	 * @return The resource defining the configuration file
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource The resource defining the configuration file
	 */
	@Required
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @return The encoding (default UTF-8)
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding The encoding (default UTF-8)
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return The objectFactory
	 */
	public LMObjectFactory getObjectFactory() {
		return objectFactory;
	}

	/**
	 * @param objectFactory The objectFactory to set
	 */
	@Required
	public void setObjectFactory(LMObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
}
