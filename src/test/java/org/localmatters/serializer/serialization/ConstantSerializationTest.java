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
 * Tests the <code>ConstantSerialization</code>
 */
public class ConstantSerializationTest extends TestCase {
	private ConstantSerialization ser;
	private Serialization parentSer;
	private Serialization delegate;
	private Writer writer;
	private Object object;
	private Object constant;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		parentSer = createMock(Serialization.class);
		delegate = createMock(Serialization.class); 
		constant = new Object();
		ser = new ConstantSerialization();
		ser.setConstant(constant);
		ser.setDelegate(delegate);
		writer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		delegate.serialize(parentSer, "name", constant, ctx);
		replay(delegate, writer);
		ser.serialize(parentSer, "name", object, ctx);
		verify(delegate, writer);
	}
}
