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
package org.localmatters.serializer.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests the <code>BeanUtilsPropertyResolver</code>
 */
public class BeanUtilsPropertyResolverTest extends TestCase {
	private BeanUtilsPropertyResolver resolver;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		resolver = new BeanUtilsPropertyResolver();
	}

	/**
	 * Tests resolving an invalid property
	 */
	public void testInvalidProperty() {
		try {
			resolver.resolve(new Dummy(), "type");
			fail("InvalidPropertyException expected");
		} catch (InvalidPropertyException e) {
		}
	}

	/**
	 * Tests resolving a valid property
	 */
	public void testValidProperty() {
		try {
			assertEquals("12345", resolver.resolve(new Dummy(), "id"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a null list property
	 */
	public void testNullListProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "photos[0]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving an empty list property
	 */
	public void testEmptyListProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "icons[0]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a non-null list property
	 */
	public void testListProperty() {
		try {
			assertEquals("we are fun", resolver.resolve(new Dummy(), "ads[1]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a null map property
	 */
	public void testNullMapProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "addresses(home)"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving an empty map property
	 */
	public void testEmptyMapProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "friends(home)"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving a map property
	 */
	public void testMapProperty() {
		try {
			assertEquals("the poo", resolver.resolve(new Dummy(), "stories(winnie)"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}
	
	/**
	 * Tests resolving a null array property
	 */
	public void testNullArrayProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "meals[0]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}
	
	/**
	 * Tests resolving an empty array property
	 */
	public void testEmptyArrayProperty() {
		try {
			assertNull(resolver.resolve(new Dummy(), "levels[0]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}
	
	/**
	 * Tests resolving an array property
	 */
	public void testArrayProperty() {
		try {
			assertEquals("truck", resolver.resolve(new Dummy(), "toys[1]"));
		} catch (InvalidPropertyException e) {
			fail("The resolution of a valid property should not result in an exception");
		}
	}

	/**
	 * Tests resolving an invalid property
	 */
	public void testInvalidIndexMappedProperty() {
		try {
			resolver.resolve(new Dummy(), "id[1]");
			fail("InvalidPropertyException expected");
		} catch (InvalidPropertyException e) {
		}
	}

    /**
     * Tests resolving a null map
     */
    public void testNullMap() {
        try {
            resolver.resolve(new Dummy(), "mixedNull(hello)[0]");
        } catch (InvalidPropertyException e) {
            fail("The resolution of a valid property should not result in an exception");
        }
    }

    /**
     * Tests resolving an empty map
     */
    public void testMapEmpty() {
        try {
            resolver.resolve(new Dummy(), "mixedEmpty(hello)[0]");
        } catch (InvalidPropertyException e) {
            fail("The resolution of a valid property should not result in an exception");
        }
    }

    /**
     * Tests resolving a missing key in the map
     */
    public void testMissingMapKey() {
        try {
            resolver.resolve(new Dummy(), "mixedMissing(hello)");
        } catch (InvalidPropertyException e) {
            fail("The resolution of a valid property should not result in an exception");
        }
    }

    /**
     * Tests resolving a missing key in the map
     */
    public void testMissingMapKeyAndFirstIndex() {
        try {
            resolver.resolve(new Dummy(), "mixedMissing(hello)[0]");
        } catch (InvalidPropertyException e) {
            fail("The resolution of a valid property should not result in an exception");
        }
    }
    
	
	/**
	 * Class describing a dummy object for testing
	 */
	public class Dummy {
		public String getId() {return "12345";}
		public String getName() {return "dummy";}
		public List<String> getPhotos() {return null;}
		public List<String> getIcons() {return Collections.emptyList();}
		public List<String> getAds() {return Arrays.asList("buy from us", "we are fun");}
		public String[] getLevels() {return new String[]{};}
		public String[] getMeals() {return null;}
		public String[] getToys() {return new String[]{"bear", "truck"};}
		public Map<String, String> getAddresses() {return null;}
		public Map<String, List<String>> getMixedNull() {return null;}
        public Map<String, List<String>> getMixedEmpty() {return new HashMap<String, List<String>>();}
        public Map<String, List<String>> getMixedMissing() {
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put("world", new ArrayList<String>());
            return map;
        }
		public Map<String, String> getFriends() {return new HashMap<String, String>();}
		public Map<String, String> getStories() {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("winnie", "the poo");
			return map;
		}
	}
}
