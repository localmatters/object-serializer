/*
   Copyright 2010-present Local Matters, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.localmatters.serializer.resolver;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.localmatters.serializer.resolver.PropertyResolver;

/**
 * Tests the <code>DispatchingPropertyResolver</code>
 */
public class DispatchingPropertyResolverTest extends TestCase {
    private DispatchingPropertyResolver resolver;
    private PropertyResolver defaultResolver;
    private PropertyResolver subResolver;
    private Object bean;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        defaultResolver = EasyMock.createMock(PropertyResolver.class);
        subResolver = EasyMock.createMock(PropertyResolver.class);
        Map<String, PropertyResolver> resolvers = new HashMap<String, PropertyResolver>();
        resolvers.put("ns", subResolver);
        resolver = new DispatchingPropertyResolver();
        resolver.setDefault(defaultResolver);
        resolver.setResolvers(resolvers);
        bean = "hello world";
    }
    
    /**
     * Tests when the property does not match the name-space
     */
    public void testWhenNotNamespaced() throws Exception {
        EasyMock.expect(defaultResolver.resolve(bean, "toto")).andReturn("doe");
        EasyMock.replay(defaultResolver, subResolver);
        assertEquals("doe", resolver.resolve(bean, "toto"));
        EasyMock.verify(defaultResolver, subResolver);
    }
    
    /**
     * Tests when the property match a name-space, but this is unknown
     */
    public void testWhenUnmatchNamespaced() throws Exception {
        EasyMock.expect(defaultResolver.resolve(bean, "john:toto")).andReturn("doe");
        EasyMock.replay(defaultResolver, subResolver);
        assertEquals("doe", resolver.resolve(bean, "john:toto"));
        EasyMock.verify(defaultResolver, subResolver);
    }
    
    /**
     * Tests when the property match a known name-space
     */
    public void testWhenNamespaced() throws Exception {
        EasyMock.expect(subResolver.resolve(bean, "toto")).andReturn("doe");
        EasyMock.replay(defaultResolver, subResolver);
        assertEquals("doe", resolver.resolve(bean, "ns:toto"));
        EasyMock.verify(defaultResolver, subResolver);
    }
    
}
