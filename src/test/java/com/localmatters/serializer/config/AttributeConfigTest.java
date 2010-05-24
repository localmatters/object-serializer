package com.localmatters.serializer.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import junit.framework.TestCase;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.Serializer;

/**
 * Tests the <code>AttributeConfig</code>
 */
public class AttributeConfigTest extends TestCase {
	private AttributeConfig config;
	private Serializer serializer;
	private Object attribute;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		config = new AttributeConfig();
		config.setName("name");
		serializer = createMock(Serializer.class);
		attribute = new Object();
		ctx = new SerializationContext(serializer, new HashMap<String, Object>(), null);
	}
	
	/**
	 * Tests handling the config
	 */
	public void testHandle() throws Exception {
		expect(serializer.serializeAttribute(config, attribute, ctx.appendSegment("name"))).andReturn(" name=\"John Doe\"");
		replay(serializer);
		String result = config.handle(attribute, ctx);
		verify(serializer);
		assertEquals(" name=\"John Doe\"", result);
	}
}
