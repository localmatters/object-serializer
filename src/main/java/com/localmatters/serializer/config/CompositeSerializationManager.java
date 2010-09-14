/**
 * File: CompositeSerializationManager.java
 *
 * Author: David Hay (dhay@localmatters.com)
 * Creation Date: Sep 14, 2010
 * Creation Time: 8:46:49 AM
 *
 * Copyright 2010 Local Matters, Inc.
 * All Rights Reserved
 *
 * Last checkin:
 *  $Author$
 *  $Revision$
 *  $Date$
 */
package com.localmatters.serializer.config;

import java.util.List;

import com.localmatters.serializer.serialization.Serialization;

/**
 * Implementation of a {@link SerializationManager} that looks for a {@link Serialization} in one or more child {@link
 * SerializationManager}s.  The first {@link Serialization} found is what is returned.  That is, if two or more {@link
 * SerializationManager}s have a {@link Serialization} for the same id, the {@link Serialization} returned by the first
 * {@link SerializationManager} in the list is the {@link Serialization} that will be returned by {@link
 * #getSerialization}
 */
public class CompositeSerializationManager implements SerializationManager {
    private List<SerializationManager> _serializationManagers;

    /**
     * Returns the list of {@link SerializationManager}s to query for a {@link Serialization}
     */
    public List<SerializationManager> getSerializationManagers() {
        return _serializationManagers;
    }

    /**
     * Set the list of {@link SerializationManager}s to query for a {@link Serialization}
     *
     * @param serializationManagers List of managers
     */
    public void setSerializationManagers(List<SerializationManager> serializationManagers) {
        _serializationManagers = serializationManagers;
    }

    /**
     * Return a {@link Serialization} for the given id. Each {@link SerializationManager} is queried for the given id.
     * The first non-null {@link Serialization} found is returned.
     *
     * @param id The id for the serialization to retrieve
     * @return The {@link Serialization} for the given id.  Otherwise, <code>null</code> if none of the {@link
     *         SerializationManager}s have a {@link Serialization} with the given id.
     */
    public Serialization getSerialization(String id) {
        for (SerializationManager manager : getSerializationManagers()) {
            Serialization serialization = manager.getSerialization(id);
            if (serialization != null) {
                return serialization;
            }
        }
        return null;
    }
}
