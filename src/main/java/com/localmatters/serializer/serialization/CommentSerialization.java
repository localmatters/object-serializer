package com.localmatters.serializer.serialization;

import org.apache.commons.lang.StringUtils;

import com.localmatters.serializer.SerializationContext;
import com.localmatters.serializer.SerializationException;

/**
 * Class handling the serialization of a comment if the serialization is pretty
 */
public class CommentSerialization implements Serialization {
	private String comment;

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#serialize(java.lang.Object, com.localmatters.serializer.SerializationContext)
	 */
	public String serialize(Object obj, SerializationContext context) throws SerializationException {
		if (context.isPretty()) {
			return context.getWriter().writeComment(this, getComment(), context);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment The comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#getName()
	 */
	public String getName() {
		return null;
	}

	/**
	 * @see com.localmatters.serializer.serialization.Serialization#isWriteEmpty()
	 */
	public boolean isWriteEmpty() {
		return false;
	}
}
