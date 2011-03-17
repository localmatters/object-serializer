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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.writer.Writer;


/**
 * Tests the <code>ValueSerialization</code>
 */
public class ValueSerializationTest extends TestCase {
	private ValueSerialization ser;
	private Serialization parentSer;
	private Writer writer;
	private Object value;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new ValueSerialization();
		parentSer = createMock(Serialization.class);
		writer = createMock(Writer.class);
		value = new Object();
		ctx = new SerializationContext(writer, null, null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		writer.writeValue(parentSer, "name", value, ctx);
		replay(writer, parentSer);
		ser.serialize(parentSer, "name", value, ctx);
		verify(writer, parentSer);
	}
}
