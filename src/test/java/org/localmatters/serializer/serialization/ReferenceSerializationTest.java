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
 * Tests the <code>ReferenceSerialization</code>
 */
public class ReferenceSerializationTest extends TestCase {
	private ReferenceSerialization ser;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ser = new ReferenceSerialization();
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		Serialization parentSer = createMock(Serialization.class);
		Serialization referenced = createMock(Serialization.class); 
		Writer writer = createMock(Writer.class);
		SerializationContext ctx = new SerializationContext(writer, null, null);
		Object object = new Object();
		ser.setReferenced(referenced);
		referenced.serialize(parentSer, "name", object, ctx);
		replay(referenced, writer, parentSer);
		ser.serialize(parentSer, "name", object, ctx);
		verify(referenced, writer, parentSer);
	}
	
	/**
	 * Tests getting the write empty when it and delegate are null
	 */
	public void testIsWriteEmptyWhenNullAndNoDelegate() throws Exception {
		assertTrue(ser.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is null, but not the delegate
	 */
	public void testIsWriteEmptyWhenNullAndDelegate() throws Exception {
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		ser.setReferenced(referenced);
		assertFalse(ser.isWriteEmpty());
	}
	
	/**
	 * Tests getting the write empty when it is not null
	 */
	public void testIsWriteEmptyWhenNotNull() throws Exception {
		ser.setWriteEmpty(Boolean.TRUE);
		ValueSerialization referenced = new ValueSerialization();
		referenced.setWriteEmpty(Boolean.FALSE);
		ser.setReferenced(referenced);
		assertTrue(ser.isWriteEmpty());
	}
	
	
}
