package com.localmatters.serializer.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>ComplexConfig</code>
 */
public class ComplexConfigTest extends TestCase {
	private ComplexConfig config;
	private Collection<Config> attributeConfigs;
	private Collection<Config> elementConfigs;
	private Serializer serializer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		attributeConfigs = new ArrayList<Config>();
		elementConfigs = new ArrayList<Config>();
		config = new ComplexConfig();
		config.setName("listing");
		config.setAttributeConfigs(attributeConfigs);
		config.setElementConfigs(elementConfigs);
		serializer = createMock(Serializer.class);
		object = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests handling the config
	 */
	public void testHandle() throws Exception {
		expect(serializer.serializeComplex(config, attributeConfigs, elementConfigs, object, ctx.appendSegment("listing"))).andReturn("<listing/>");
		replay(serializer);
		String result = config.handle(object, ctx);
		verify(serializer);
		assertEquals("<listing/>", result);
	}
}
