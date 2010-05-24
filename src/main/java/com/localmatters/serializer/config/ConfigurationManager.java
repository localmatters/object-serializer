package com.localmatters.serializer.config;

/**
 * Interface describing a configuration manager
 */
public interface ConfigurationManager {

	/**
	 * Returns the configuration with the given ID
	 * @param id The id for the configuration to retrieve
	 * @return The corresponding configuration or null
	 */
	public Config getConfig(String id);
}
