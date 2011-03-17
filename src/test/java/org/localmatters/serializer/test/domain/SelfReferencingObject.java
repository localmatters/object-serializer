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

/**
 * A self referencing class
 */
public class SelfReferencingObject {
	private String name;
	private List<SelfReferencingObject> others;
	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return The others
	 */
	public List<SelfReferencingObject> getOthers() {
		return others;
	}
	/**
	 * @param others The others to set
	 */
	public void setOthers(List<SelfReferencingObject> others) {
		this.others = others;
	}
}
