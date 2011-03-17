package org.localmatters.serializer.serialization;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.serialization.NameSerialization;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.writer.Writer;

import junit.framework.TestCase;


/**
 * Tests the <code>NameSerialization</code>
 */
public class NameSerializationTest extends TestCase {
	private NameSerialization ser;
	private Serialization parentSer;
	private Serialization delegate;
	private Writer writer;
	private Object object;
	private SerializationContext ctx;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		delegate = createMock(Serialization.class); 
		parentSer = createMock(Serialization.class);
		ser = new NameSerialization();
		ser.setName("newName");
		ser.setDelegate(delegate);
		writer = createMock(Writer.class);
		object = new Object();
		ctx = new SerializationContext(writer, null, null);
	}

	/**
	 * Tests the serialization
	 */
	public void testHandle() throws Exception {
		delegate.serialize(parentSer, "newName", object, ctx);
		replay(delegate, writer);
		ser.serialize(parentSer, "name", object, ctx);
		verify(delegate, writer);
	}
    
    /**
     * Tests the remove default name
     */
    public void testRemoveDefaultName() {
        expect(delegate.removeDefaultName()).andReturn("superHeroName");
        replay(delegate);
        assertSame("newName", ser.removeDefaultName());
        verify(delegate);
        assertNull(ser.getName());
    }
	
}
