/**
 * 
 */
package com.localmatters.serializer.test.domain;

import java.util.List;
import java.util.Map;

/**
 * A test object with many generic variables.
 */
public class ObjectWithGenerics {
	@SuppressWarnings("unchecked")
	private List list;
	private List<String> listOfString;
	private List<List<String>> listOfListOfString;
	private List<ParameterizedObject> listOfParameterizedObject;
	@SuppressWarnings("unchecked")
	private List<Map<String, List>> listOfMapOfStringAndList;
	private Map<?, ?> map;
	private Map<String, Double> mapOfStringAndDouble;

	/**
	 * @return The list
	 */
	@SuppressWarnings("unchecked")
	public List getList() {
		return list;
	}
	/**
	 * @param list The list to set
	 */
	@SuppressWarnings("unchecked")
	public void setList(List list) {
		this.list = list;
	}
	/**
	 * @return The listOfString
	 */
	public List<String> getListOfString() {
		return listOfString;
	}
	/**
	 * @param listOfString The listOfString to set
	 */
	public void setListOfString(List<String> listOfString) {
		this.listOfString = listOfString;
	}
	/**
	 * @return The listOfMapOfStringAndList
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, List>> getListOfMapOfStringAndList() {
		return listOfMapOfStringAndList;
	}
	/**
	 * @param listOfMapOfStringAndList The listOfMapOfStringAndList to set
	 */
	@SuppressWarnings("unchecked")
	public void setListOfMapOfStringAndList(
			List<Map<String, List>> listOfMapOfStringAndList) {
		this.listOfMapOfStringAndList = listOfMapOfStringAndList;
	}
	/**
	 * @return The map
	 */
	public Map<?,?> getMap() {
		return map;
	}
	/**
	 * @param map The map to set
	 */
	public void setMap(Map<?,?> map) {
		this.map = map;
	}
	/**
	 * @return The mapOfStringAndDouble
	 */
	public Map<String, Double> getMapOfStringAndDouble() {
		return mapOfStringAndDouble;
	}
	/**
	 * @param mapOfStringAndDouble The mapOfStringAndDouble to set
	 */
	public void setMapOfStringAndDouble(Map<String, Double> mapOfStringAndDouble) {
		this.mapOfStringAndDouble = mapOfStringAndDouble;
	}
	/**
	 * @return The listOfListOfString
	 */
	public List<List<String>> getListOfListOfString() {
		return listOfListOfString;
	}
	/**
	 * @param listOfListOfString The listOfListOfString to set
	 */
	public void setListOfListOfString(List<List<String>> listOfListOfString) {
		this.listOfListOfString = listOfListOfString;
	}
	/**
	 * @return The listOfParameterizedObject
	 */
	public List<ParameterizedObject> getListOfParameterizedObject() {
		return listOfParameterizedObject;
	}
	/**
	 * @param listOfParameterizedObject The listOfParameterizedObject to set
	 */
	public void setListOfParameterizedObject(
			List<ParameterizedObject> listOfParameterizedObject) {
		this.listOfParameterizedObject = listOfParameterizedObject;
	}
}
