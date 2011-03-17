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
package org.localmatters.serializer.util;

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
