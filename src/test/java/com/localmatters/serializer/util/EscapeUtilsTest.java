package com.localmatters.serializer.util;

import junit.framework.TestCase;

/**
 * Tests the <code>EscapeUtils</code>
 */
public class EscapeUtilsTest extends TestCase {
	
	/**
	 * Tests escaping strings
	 */
	public void testEscape() {
		assertNotNull(new EscapeUtils());
		assertEquals("12345 \\\"Hotel\\\" & spa", EscapeUtils.escapeJson("12345 \"Hotel\" & spa"));
		assertEquals("\\b\\n\\t\\f\\r\u0001", EscapeUtils.escapeJson("\b\n\t\f\r\u0001"));
		assertEquals("hotel \\\\ spa", EscapeUtils.escapeJson("hotel \\ spa"));
		assertEquals("hotel / spa", EscapeUtils.escapeJson("hotel / spa"));
		assertEquals(null, EscapeUtils.escapeJson(null));
	}

}
