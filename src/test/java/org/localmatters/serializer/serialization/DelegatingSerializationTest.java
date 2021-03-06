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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

/**
 * Tests the <code>DelegatingSerialization</code>
 */
public class DelegatingSerializationTest extends TestCase {
	private DelegatingSerialization serialization;
	private Serialization delegate;

	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {
		delegate = createMock(Serialization.class);
		serialization = new BeanSerialization();
		serialization.setDelegate(delegate);
	}
	
	/**
	 * Tests getting the write empty
	 */
	public void testIsWriteEmpty() {
		expect(delegate.isWriteEmpty()).andReturn(false);
		replay(delegate);
		assertFalse(serialization.isWriteEmpty());
		verify(delegate);
		reset(delegate);
		expect(delegate.isWriteEmpty()).andReturn(true);
		replay(delegate);
		assertTrue(serialization.isWriteEmpty());
		verify(delegate);
	}
	
	/**
	 * Tests the get context less serialization
	 */
	public void testGetContextlessSerialization() {
		ValueSerialization value = new ValueSerialization();
		expect(delegate.getContextlessSerialization()).andReturn(value);
		replay(delegate);
		assertSame(value, serialization.getContextlessSerialization());
		verify(delegate);
	}
    
    /**
     * Tests the remove default name
     */
    public void testRemoveDefaultName() {
        expect(delegate.removeDefaultName()).andReturn("superHeroName");
        replay(delegate);
        assertSame("superHeroName", serialization.removeDefaultName());
        verify(delegate);
    }
}
