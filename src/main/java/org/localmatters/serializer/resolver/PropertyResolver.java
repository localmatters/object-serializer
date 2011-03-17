package org.localmatters.serializer.resolver;

/**
 * Interface defining a resolver that can resolves a bean (obejct) property
 */
public interface PropertyResolver {

	/**
	 * Resolves a property of the provided bean
	 * @param bean The bean to resolve the property of
	 * @param property The property (simple, mapped, index, etc.) to resolved
	 * @return The corresponding property or null if the property is null
	 * @throws PropertyResolverException When the resolution fails 
	 */
	public Object resolve(Object bean, String property) throws PropertyResolverException;
}
