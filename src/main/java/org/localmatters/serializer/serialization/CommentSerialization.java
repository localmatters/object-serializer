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
