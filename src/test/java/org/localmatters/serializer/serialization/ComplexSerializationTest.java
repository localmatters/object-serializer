package org.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.serialization.ComplexSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.writer.Writer;

import junit.framework.TestCase;


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
