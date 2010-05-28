/**
 * 
 */
package com.localmatters.serializer.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.collections.CollectionUtils;

import com.localmatters.serializer.test.domain.DummyEnum;
import com.localmatters.serializer.test.domain.DummyObject;
import com.localmatters.serializer.test.domain.ObjectWithGenerics;
import com.localmatters.serializer.test.domain.ParameterizedObject;
import com.localmatters.serializer.test.domain.DummyObject.Address;
import com.localmatters.serializer.test.domain.DummyObject.Orders;

/**
 * Tests the <code>ReflectionUtils</code>
 */
public class ReflectionUtilsTest extends TestCase {

	/**
	 * Tests the instantiation of this class (for code completion)
	 */
	public void testInstantiation() {
		assertNotNull(new ReflectionUtils() {});
	}
	
	/**
	 * Tests getting the primitive class
	 */
	public void testGetPrimitiveClass() {
		assertSame(boolean.class, ReflectionUtils.getPrimitiveClass(Boolean.class));
		assertSame(float.class, ReflectionUtils.getPrimitiveClass(Float.class));
		assertSame(long.class, ReflectionUtils.getPrimitiveClass(Long.class));
		assertSame(int.class, ReflectionUtils.getPrimitiveClass(Integer.class));
		assertSame(short.class, ReflectionUtils.getPrimitiveClass(Short.class));
		assertSame(byte.class, ReflectionUtils.getPrimitiveClass(Byte.class));
		assertSame(double.class, ReflectionUtils.getPrimitiveClass(Double.class));
		assertSame(char.class, ReflectionUtils.getPrimitiveClass(Character.class));
		assertNull(ReflectionUtils.getPrimitiveClass(int.class));
		assertNull(ReflectionUtils.getPrimitiveClass(String.class));
		assertNull(ReflectionUtils.getPrimitiveClass(Date.class));
		assertNull(ReflectionUtils.getPrimitiveClass(DummyEnum.class));
		assertNull(ReflectionUtils.getPrimitiveClass(DummyObject.class));
		assertNull(ReflectionUtils.getPrimitiveClass(ReflectionUtils.class));
		assertNull(ReflectionUtils.getPrimitiveClass(Object.class));
	}
	
	/**
	 * Tests checking whether a class is a primitive number or one of their 
	 * equivalent wrapper classes
	 */
	public void testIsNumeric() {
		assertTrue(ReflectionUtils.isNumeric(Float.class));
		assertTrue(ReflectionUtils.isNumeric(float.class));
		assertTrue(ReflectionUtils.isNumeric(Long.class));
		assertTrue(ReflectionUtils.isNumeric(long.class));
		assertTrue(ReflectionUtils.isNumeric(Integer.class));
		assertTrue(ReflectionUtils.isNumeric(int.class));
		assertTrue(ReflectionUtils.isNumeric(Short.class));
		assertTrue(ReflectionUtils.isNumeric(short.class));
		assertTrue(ReflectionUtils.isNumeric(Double.class));
		assertTrue(ReflectionUtils.isNumeric(double.class));
		assertTrue(ReflectionUtils.isNumeric(Byte.class));
		assertTrue(ReflectionUtils.isNumeric(byte.class));

		assertFalse(ReflectionUtils.isNumeric(Boolean.class));
		assertFalse(ReflectionUtils.isNumeric(boolean.class));
		assertFalse(ReflectionUtils.isNumeric(Character.class));
		assertFalse(ReflectionUtils.isNumeric(char.class));
		assertFalse(ReflectionUtils.isNumeric(String.class));
		assertFalse(ReflectionUtils.isNumeric(Date.class));
		assertFalse(ReflectionUtils.isNumeric(DummyEnum.class));
		assertFalse(ReflectionUtils.isNumeric(DummyObject.class));
		assertFalse(ReflectionUtils.isNumeric(ReflectionUtils.class));
		assertFalse(ReflectionUtils.isNumeric(Object.class));
	}
	
	/**
	 * Tests checking whether a class is the primitive boolean or its Boolean
	 * wrapper
	 */
	public void testIsBoolean() {
		assertTrue(ReflectionUtils.isBoolean(Boolean.class));
		assertTrue(ReflectionUtils.isBoolean(boolean.class));

		assertFalse(ReflectionUtils.isBoolean(Float.class));
		assertFalse(ReflectionUtils.isBoolean(float.class));
		assertFalse(ReflectionUtils.isBoolean(Long.class));
		assertFalse(ReflectionUtils.isBoolean(long.class));
		assertFalse(ReflectionUtils.isBoolean(Integer.class));
		assertFalse(ReflectionUtils.isBoolean(int.class));
		assertFalse(ReflectionUtils.isBoolean(Short.class));
		assertFalse(ReflectionUtils.isBoolean(short.class));
		assertFalse(ReflectionUtils.isBoolean(Double.class));
		assertFalse(ReflectionUtils.isBoolean(double.class));
		assertFalse(ReflectionUtils.isBoolean(Byte.class));
		assertFalse(ReflectionUtils.isBoolean(byte.class));
		assertFalse(ReflectionUtils.isBoolean(Character.class));
		assertFalse(ReflectionUtils.isBoolean(char.class));
		assertFalse(ReflectionUtils.isBoolean(String.class));
		assertFalse(ReflectionUtils.isBoolean(Date.class));
		assertFalse(ReflectionUtils.isBoolean(DummyEnum.class));
		assertFalse(ReflectionUtils.isBoolean(DummyObject.class));
		assertFalse(ReflectionUtils.isBoolean(ReflectionUtils.class));
		assertFalse(ReflectionUtils.isBoolean(Object.class));
	}
	
	/**
	 * Tests checking whether a class is a primitive or one of their equivalent 
	 * wrapper classes
	 */
	public void testIsPrimitiveOrWrapper() {
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Float.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(float.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Long.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(long.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Integer.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(int.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Short.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(short.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Double.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(double.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Byte.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(byte.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Boolean.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(boolean.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(Character.class));
		assertTrue(ReflectionUtils.isPrimitiveOrWrapper(char.class));

		assertFalse(ReflectionUtils.isNumeric(String.class));
		assertFalse(ReflectionUtils.isNumeric(Date.class));
		assertFalse(ReflectionUtils.isNumeric(DummyEnum.class));
		assertFalse(ReflectionUtils.isNumeric(DummyObject.class));
		assertFalse(ReflectionUtils.isNumeric(ReflectionUtils.class));
		assertFalse(ReflectionUtils.isNumeric(Object.class));
	}
	
	/**
	 * Tests checking whether the instances of the given class can be represent
	 * by a simple value
	 */
	public void testIsSimple() {
		assertTrue(ReflectionUtils.isSimple(Float.class));
		assertTrue(ReflectionUtils.isSimple(float.class));
		assertTrue(ReflectionUtils.isSimple(Long.class));
		assertTrue(ReflectionUtils.isSimple(long.class));
		assertTrue(ReflectionUtils.isSimple(Integer.class));
		assertTrue(ReflectionUtils.isSimple(int.class));
		assertTrue(ReflectionUtils.isSimple(Short.class));
		assertTrue(ReflectionUtils.isSimple(short.class));
		assertTrue(ReflectionUtils.isSimple(Double.class));
		assertTrue(ReflectionUtils.isSimple(double.class));
		assertTrue(ReflectionUtils.isSimple(Byte.class));
		assertTrue(ReflectionUtils.isSimple(byte.class));
		assertTrue(ReflectionUtils.isSimple(Boolean.class));
		assertTrue(ReflectionUtils.isSimple(boolean.class));
		assertTrue(ReflectionUtils.isSimple(Character.class));
		assertTrue(ReflectionUtils.isSimple(char.class));
		assertTrue(ReflectionUtils.isSimple(String.class));
		assertTrue(ReflectionUtils.isSimple(Date.class));
		assertTrue(ReflectionUtils.isSimple(DummyEnum.class));

		assertFalse(ReflectionUtils.isSimple(Object.class));
		assertFalse(ReflectionUtils.isSimple(DummyObject.class));
		assertFalse(ReflectionUtils.isSimple(ReflectionUtils.class));
	}
	
	/**
	 * Test getting the type arguments for a type
	 * @throws Exception When the test fails
	 */
	public void testGetTypeArgumentsForType() throws Exception {
		assertNull(ReflectionUtils.getTypeArgumentsForType(String.class));
		assertNull(ReflectionUtils.getTypeArgumentsForType(List.class));
		assertNull(ReflectionUtils.getTypeArgumentsForType(Map.class));

		Type[] types = ReflectionUtils.getTypeArgumentsForType(ParameterizedObject.class);
		assertNotNull(types);
		assertEquals(1, types.length);
		assertSame(String.class, types[0]);
		
		Class<?> cl = ObjectWithGenerics.class;

		Method method = cl.getMethod("getList", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNull(types);

		method = cl.getMethod("getListOfString", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(1, types.length);
		assertSame(String.class, types[0]);

		method = cl.getMethod("getListOfListOfString", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof ParameterizedType);
		ParameterizedType type = (ParameterizedType) types[0];
		assertSame(List.class, type.getRawType());
		types = ReflectionUtils.getTypeArgumentsForType(type);
		assertNotNull(types);
		assertEquals(1, types.length);
		assertSame(String.class, types[0]);

		method = cl.getMethod("getListOfParameterizedObject", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof Class<?>);
		Class<?> klass = (Class<?>) types[0];
		types = ReflectionUtils.getTypeArgumentsForType(klass);
		assertNotNull(types);
		assertEquals(1, types.length);
		assertSame(String.class, types[0]);

		method = cl.getMethod("getListOfMapOfStringAndList", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof ParameterizedType);
		type = (ParameterizedType) types[0];
		assertSame(Map.class, type.getRawType());
		types = ReflectionUtils.getTypeArgumentsForType(type);
		assertNotNull(types);
		assertEquals(2, types.length);
		assertSame(String.class, types[0]);
		assertSame(List.class, types[1]);

		method = cl.getMethod("getMap", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(2, types.length);
		assertFalse(types[0] instanceof Class<?>);
		assertFalse(types[0] instanceof ParameterizedType);
		assertFalse(types[1] instanceof Class<?>);
		assertFalse(types[1] instanceof ParameterizedType);

		method = cl.getMethod("getMapOfStringAndDouble", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(2, types.length);
		assertTrue(types[0] instanceof Class<?>);
		assertSame(String.class, types[0]);
		assertTrue(types[1] instanceof Class<?>);
		assertSame(Double.class, types[1]);

		method = cl.getMethod("getArray", (Class<?>[]) null);
		types = ReflectionUtils.getTypeArgumentsForType(method.getGenericReturnType());
		assertNotNull(types);
		assertEquals(1, types.length);
		assertTrue(types[0] instanceof Class<?>);
		assertSame(String.class, types[0]);
	}
	
	/**
	 * Tests retrieving the getter methods of a class
	 */
	public void testGetGetters() {
		Collection<Method> getters = ReflectionUtils.getGetters(DummyObject.class);
		assertEquals(7, CollectionUtils.size(getters));
		Iterator<Method> itr = getters.iterator();
		assertEquals("getAddresses", itr.next().getName());
		assertEquals("getAddressesRaw", itr.next().getName());
		assertEquals("getId", itr.next().getName());
		assertEquals("getName", itr.next().getName());
		assertEquals("getOrders", itr.next().getName());
		assertEquals("getOrdersByAddresses", itr.next().getName());
		assertEquals("getOrdersList", itr.next().getName());
		assertFalse(itr.hasNext());
		
		getters = ReflectionUtils.getGetters(Address.class);
		assertEquals(5, CollectionUtils.size(getters));
		itr = getters.iterator();
		assertEquals("getCity", itr.next().getName());
		assertEquals("getState", itr.next().getName());
		assertEquals("getStreet", itr.next().getName());
		assertEquals("getZ", itr.next().getName());
		assertEquals("getZip", itr.next().getName());
		assertFalse(itr.hasNext());
		
		getters = ReflectionUtils.getGetters(Orders.class);
		assertEquals(1, CollectionUtils.size(getters));
		itr = getters.iterator();
		assertEquals("isEmpty", itr.next().getName());
		assertFalse(itr.hasNext());
	}
	
	/**
	 * Tests retrieving the getter methods field name
	 */
	public void testGetGetterFieldName() throws Exception {
		Collection<Method> getters = ReflectionUtils.getGetters(Address.class);
		assertEquals(5, CollectionUtils.size(getters));
		Iterator<Method> itr = getters.iterator();
		assertEquals("city", ReflectionUtils.getGetterFieldName(itr.next()));
		assertEquals("state", ReflectionUtils.getGetterFieldName(itr.next()));
		assertEquals("street", ReflectionUtils.getGetterFieldName(itr.next()));
		assertEquals("z", ReflectionUtils.getGetterFieldName(itr.next()));
		assertEquals("zip", ReflectionUtils.getGetterFieldName(itr.next()));
		assertFalse(itr.hasNext());
		assertNull(ReflectionUtils.getGetterFieldName(Address.class.getMethod("getClass", (Class<?>[]) null)));
		assertNull(ReflectionUtils.getGetterFieldName(Address.class.getMethod("toString", (Class<?>[]) null)));
	}
	
}
