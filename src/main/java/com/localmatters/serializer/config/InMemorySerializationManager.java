package com.localmatters.serializer.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import com.localmatters.serializer.config.parser.SerializationParser;
import com.localmatters.serializer.config.parser.XmlSerializationParser;
import com.localmatters.serializer.serialization.Serialization;
import com.localmatters.util.objectfactory.LMObjectFactory;
import com.localmatters.util.refreshable.AbstractRefreshableContent;

/**
 * A refreshable in memory serialization manager
 */
public class InMemorySerializationManager extends AbstractRefreshableContent implements SerializationManager {
	protected static final String DEFAULT_ENCODING = "UTF-8";

	private Resource resource;
	private Map<String, Serialization> serializations = Collections.emptyMap();
	private boolean alwaysRefresh;
    private SerializationParser _serializationParser = new XmlSerializationParser();

	/**
	 * @see com.localmatters.util.refreshable.AbstractRefreshableContent#doRefresh()
	 */
    @Override
    protected void doRefresh() throws Exception {
        this.serializations = _serializationParser.parse(getResource());
    }

    /**
	 * @see com.localmatters.serializer.config.SerializationManager#getSerialization(java.lang.String)
	 */
	public Serialization getSerialization(String id) {
		if (isAlwaysRefresh()) {
			refresh();
		}
		return serializations.get(id);
	}

    /**
     * Adds a new {@link Serialization} to this manager under the specified id.  If there is an existing serialization
     * with the id already registered, the old Serialization instance is returned.
     *
     * @param id            The id to register the serialization under
     * @param serialization The serialization to register.
     * @return The old {@link Serialization} with the given id, if any.  <code>null</code> otherwise.
     */
    public Serialization addSerialization(String id, Serialization serialization) {
        return serializations.put(id, serialization);
    }

    /**
     * Removes a {@link Serialization} with the given id from this manager.  The serialization that was removed is
     * returned.
     *
     * @param id The serialization to remove
     * @return The {@link Serialization} that was removed.  If no such serialization exists with the given id,
     *         <code>null</code> is returned.
     */
    public Serialization removeSerialization(String id) {
        return serializations.remove(id);
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
        if (_serializationParser instanceof XmlSerializationParser) {
            return ((XmlSerializationParser) _serializationParser).getDefaultEncoding();
        }
        else {
            throw new UnsupportedOperationException(
                "Unable to get encoding from SerializationParser of type:" + _serializationParser.getClass().getName());
        }
	}

	/**
	 * @param encoding The encoding (default UTF-8)
	 */
	public void setEncoding(String encoding) {
        if (_serializationParser instanceof XmlSerializationParser) {
            ((XmlSerializationParser) _serializationParser).setDefaultEncoding(encoding);
        }
        else {
            throw new UnsupportedOperationException(
                "Unable to set encoding on SerializationParser of type:" + _serializationParser.getClass().getName());
        }
	}

	/**
	 * @return The objectFactory
	 */
	public LMObjectFactory getObjectFactory() {
        if (_serializationParser instanceof XmlSerializationParser) {
            return ((XmlSerializationParser) _serializationParser).getObjectFactory();
        }
        else {
            throw new UnsupportedOperationException(
                "Unable to get object factory from SerializationParser of type:" +
                _serializationParser.getClass().getName());
        }
	}

	/**
	 * @param objectFactory The objectFactory to set
	 */
	public void setObjectFactory(LMObjectFactory objectFactory) {
        if (_serializationParser instanceof XmlSerializationParser) {
            ((XmlSerializationParser) _serializationParser).setObjectFactory(objectFactory);
        }
        else {
            throw new UnsupportedOperationException(
                "Unable to set object factory on SerializationParser of type:" +
                _serializationParser.getClass().getName());
        }
	}

	/**
	 * @return Whether the configuration should always be refreshed (this should
	 * be true ONLY for development)
	 */
	public boolean isAlwaysRefresh() {
		return alwaysRefresh;
	}

	/**
	 * @param alwaysRefresh Whether the configuration should always be refreshed
	 * (this should be true ONLY for development)
	 */
	public void setAlwaysRefresh(boolean alwaysRefresh) {
		this.alwaysRefresh = alwaysRefresh;
	}
}
