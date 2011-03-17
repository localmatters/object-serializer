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
package org.localmatters.serializer.test.domain;

import java.util.List;
import java.util.Map;

/**
 * A test object with many generic variables.
 */
public class ObjectWithGenerics {
	@SuppressWarnings("rawtypes")
	private List list;
	private List<String> listOfString;
	private List<List<String>> listOfListOfString;
	private List<ParameterizedObject> listOfParameterizedObject;
	@SuppressWarnings("rawtypes")
	private List<Map<String, List>> listOfMapOfStringAndList;
	private Map<?, ?> map;
	private Map<String, Double> mapOfStringAndDouble;
	private String[] array;

	/**
	 * @return The list
	 */
	@SuppressWarnings("rawtypes")
	public List getList() {
		return list;
	}
	/**
	 * @param list The list to set
	 */
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
	public List<Map<String, List>> getListOfMapOfStringAndList() {
		return listOfMapOfStringAndList;
	}
	/**
	 * @param listOfMapOfStringAndList The listOfMapOfStringAndList to set
	 */
	@SuppressWarnings("rawtypes")
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
	/**
	 * @return The array
	 */
	public String[] getArray() {
		return array;
	}
	/**
	 * @param array The array to set
	 */
	public void setArray(String[] array) {
		this.array = array;
	}
}
