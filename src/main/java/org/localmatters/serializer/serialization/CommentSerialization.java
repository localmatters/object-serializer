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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class defining a serialization that can have comments
 */
public abstract class CommentSerialization extends AbstractSerialization {
	private List<String> comments;

	/**
	 * @return The comments on this serialization
	 */
	public List<String> getComments() {
		if (comments == null) {
			setComments(new ArrayList<String>());
		}
		return comments;
	}

	/**
	 * @param comment The comment on this serialization to add
	 */
	public void addComment(String comment) {
		getComments().add(comment);
	}

	/**
	 * @param comments The comments on this serialization
	 */
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
}
