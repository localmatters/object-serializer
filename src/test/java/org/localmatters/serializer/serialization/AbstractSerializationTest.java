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
package org.localmatters.serializer.serialization;

import junit.framework.TestCase;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;

/**
 * Tests the <code>AbstractSerialization</code>
 */
public class AbstractSerializationTest extends TestCase {
	private AbstractSerialization serialization;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		serialization = new AbstractSerialization() {
			public void serialize(Serialization ser, String name, Object obj,
					SerializationContext ctx) throws SerializationException {
			}
		};
	}

	/**
	 * Tests the is write empty method
	 */
	public void testIsWriteEmpty() {
		serialization.setWriteEmpty(null);
		assertFalse(serialization.isWriteEmpty());
		assertNull(serialization.getWriteEmpty());
		serialization.setWriteEmpty(Boolean.TRUE);
		assertTrue(serialization.isWriteEmpty());
		assertSame(Boolean.TRUE, serialization.getWriteEmpty());
		serialization.setWriteEmpty(Boolean.FALSE);
		assertFalse(serialization.isWriteEmpty());
		assertSame(Boolean.FALSE, serialization.getWriteEmpty());
	}
	
	/**
	 * Tests the get context less serialization
	 */
	public void testGetContextlessSerialization() {
		assertSame(serialization, serialization.getContextlessSerialization());
	}
}
