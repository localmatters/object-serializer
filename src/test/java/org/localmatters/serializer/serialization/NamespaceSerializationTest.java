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
 * Tests the <code>NamespaceSerialization</code>
 */
public class NamespaceSerializationTest extends TestCase {
	private NamespaceSerialization ser;
	private Serialization parentSer;
	private Writer writer;
	private Object attribute;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new NamespaceSerialization();
		parentSer = createMock(Serialization.class);
		writer = createMock(Writer.class);
		attribute = new Object();
		ctx = new SerializationContext(writer, null, null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		writer.writeNamespace(parentSer, "name", attribute, ctx);
		replay(writer);
		ser.serialize(parentSer, "name", attribute, ctx);
		verify(writer);
	}
}
