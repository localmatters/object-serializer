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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.writer.Writer;


/**
 * Tests the <code>ComplexSerialization</code>
 */
public class ComplexSerializationTest extends TestCase {
	private ComplexSerialization ser;
	private Serialization parentSer;
	private List<Serialization> attributes;
	private List<Serialization> elements;
	private List<String> comments;
	private Writer writer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		parentSer = createMock(Serialization.class);
		attributes = new ArrayList<Serialization>();
		elements = new ArrayList<Serialization>();
		comments = new ArrayList<String>();
		ser = new ComplexSerialization();
		ser.setAttributes(attributes);
		ser.setElements(elements);
		ser.setComments(comments);
		writer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(writer, null, null);
	}
	
	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		writer.writeComplex(parentSer, "listing", object, attributes, elements, comments, ctx);
		replay(writer);
		ser.serialize(parentSer, "listing", object, ctx);
		verify(writer);
	}
}
