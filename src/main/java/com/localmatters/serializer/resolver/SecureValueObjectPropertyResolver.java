package com.localmatters.serializer.resolver;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import com.localmatters.mvc.transfer.ValueObject;
import com.localmatters.util.CollectionUtils;

/**
 * <p>A delegating resolver that will check that a property is public if the 
 * bean is a <code>ValueObject</code>. If the property is not public, then it 
 * will throw a <code>PrivatePropertyException</code>. If the property is public
 * or if the bean is not a value object, then this resolver will call the 
 * delegate to resolve the property.</p>
 * 
 * <p>The list of public property for a value object are defined by a collection
 * that is associated to the attribute key of the value object. If there are no
 * collection for a value object or this collection is empty, then the value
 * object itself is consider not public.</p>  
 */
public class SecureValueObjectPropertyResolver extends DelegatingPropertyResolver {
	private Map<String, Collection<String>> publicProperties;
	private Pattern indexedMappedRemoverPattern;

	/**
	 * @see com.localmatters.serializer.resolver.PropertyResolver#resolve(java.lang.Object, java.lang.String)
	 */
	public Object resolve(Object bean, String property) throws PropertyResolverException {
		if (bean instanceof ValueObject) {
			ValueObject vo = (ValueObject) bean;
			
			String variable = property;
			Matcher matcher = getIndexedMappedRemoverPattern().matcher(property);
			if (matcher.find()) {
				variable = matcher.group(1);
			}
			
			Collection<String> properties = getPublicProperties().get(vo.getAttributeKey());
			if (!CollectionUtils.contains(properties, variable)) {
				throw new NotPublicPropertyException(variable, bean.getClass().getName());
			}
		}
		return getDelegate().resolve(bean, property);
	}

	/**
	 * @return The map of public properties for each value object (identified
	 * by their attribute key)
	 */
	public Map<String, Collection<String>> getPublicProperties() {
		return publicProperties;
	}

	/**
	 * @param publicProperties The map of public properties for each value 
	 * object (identified by their attribute key)
	 */
	@Required
	public void setPublicProperties(Map<String, Collection<String>> publicProperties) {
		this.publicProperties = publicProperties;
	}

	/**
	 * @return The pattern to clean the property from any map or index 
	 * information. This pattern is expected to have a capturing group of the
	 * clean part of the property.
	 */
	public Pattern getIndexedMappedRemoverPattern() {
		return indexedMappedRemoverPattern;
	}

	/**
	 * @param indexedMappedRemoverPattern The pattern to clean the property from
	 * any map or index information. This pattern is expected to have a 
	 * capturing group of the clean part of the property.
	 */
	@Required
	public void setIndexedMappedRemoverPattern(Pattern indexedMappedRemoverPattern) {
		this.indexedMappedRemoverPattern = indexedMappedRemoverPattern;
	}
}
