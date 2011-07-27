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
package org.localmatters.serializer.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.localmatters.serializer.SerializationContext;
import org.localmatters.serializer.SerializationException;
import org.localmatters.serializer.serialization.Serialization;
import org.localmatters.serializer.util.EscapeUtils;


/**
 * This class defines a serialization writer that outputs XML.
 */
public class XMLWriter extends AbstractWriter {
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String STAR_STAR = "**";
    private static final String DASH_DASH = "--";
    private static final String MIDDLE_COMMENT = "     ";
    private static final String END_COMMENT = " -->";
    private static final String START_COMMENT = "<!-- ";
    private static final String NEW_LINE = "\n";
    private static final String VALUE_LEVEL = "value";
	private static final byte[] ROOT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes();
	private static final byte[] LT_BYTES = LT.getBytes();
	private static final byte[] LT_SLASH_BYTES = "</".getBytes();
	private static final byte[] GT_BYTES = GT.getBytes();
	private static final byte[] SLASH_GT_BYTES = "/>".getBytes();
	private static final byte[] SPACE_BYTES = " ".getBytes();
	private static final byte[] EQUALS_QUOTE_BYTES = "=\"".getBytes();
	private static final byte[] QUOTE_BYTES = "\"".getBytes();
	private static final String INDENTATION = "    ";
	private Map<Integer, String> prefixes = new HashMap<Integer, String>();

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeRoot(org.localmatters.serializer.serialization.Serialization, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeRoot(Serialization ser, Object root, SerializationContext ctx) throws SerializationException {
		write(ctx, ROOT);
		ser.serialize(ser, null, root, ctx);
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeValue(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeValue(Serialization ser, 
			String name,
			Object value, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(StringUtils.defaultIfEmpty(name, VALUE_LEVEL));

		String prefix = getPrefix(ctx);
		String valueStr = String.valueOf(value);
		if ((value != null) && (StringUtils.isNotEmpty(valueStr))) {
            String str = EscapeUtils.escapeXml(valueStr);
			if (StringUtils.isNotBlank(name)) {
				write(ctx, prefix)
				.write(ctx, LT_BYTES).write(ctx, name).write(ctx, GT_BYTES)
				.write(ctx, str)
				.write(ctx, LT_SLASH_BYTES).write(ctx, name).write(ctx, GT_BYTES);
			} else {
				write(ctx, str);
			}
		} else if (ser.isWriteEmpty() && StringUtils.isNotBlank(name)) {
			write(ctx, prefix).write(ctx, LT_BYTES).write(ctx, name).write(ctx, SLASH_GT_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeAttribute(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeAttribute(Serialization ser, 
			String name,
			Object attribute, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(checkRequiredName(ctx, name));

		if (attribute != null) {
			write(ctx, SPACE_BYTES)
			.write(ctx, name)
			.write(ctx, EQUALS_QUOTE_BYTES)
			.write(ctx, EscapeUtils.escapeXml(String.valueOf(attribute)))
			.write(ctx, QUOTE_BYTES);
		} else if (ser.isWriteEmpty()) {
			write(ctx, SPACE_BYTES)
			.write(ctx, name)
			.write(ctx, EQUALS_QUOTE_BYTES)
			.write(ctx, QUOTE_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeNamespace(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, org.localmatters.serializer.SerializationContext)
	 */
	public void writeNamespace(Serialization ser, String name,
	        Object namespace, SerializationContext ctx)
	        throws SerializationException {
	    writeAttribute(ser, name, namespace, ctx);
	}
	
	/**
	 * @see org.localmatters.serializer.writer.Writer#writeComplex(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.lang.Object, java.util.Collection, java.util.Collection, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	public void writeComplex(Serialization ser, 
			String name,
			Object object, 
			Collection<Serialization> attributes, 
			Collection<Serialization> elements, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(checkRequiredName(ctx, name));
        boolean empty = true;

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(attributes) || CollectionUtils.isNotEmpty(elements)) {
		    ctx.pushPrefix(processComments(ctx, prefix, comments) + LT + name);

			// writes the attributes
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Serialization attribute : attributes) {
					attribute.serialize(attribute, null, object, ctx);
				}
			}

			if (CollectionUtils.isEmpty(elements)) {
			    if (ctx.consomePrefix() == null) {
			        empty = false;
                    write(ctx, SLASH_GT_BYTES);
                }
            } else {
                if (ctx.consomePrefix() == null) {
                    empty = false;
                    write(ctx, GT_BYTES);
                } else {
                    ctx.pushPrefix(processComments(ctx, prefix, comments) + LT + name + GT);
                }
                for (Serialization element : elements) {
                    element.serialize(element, null, object, ctx);
                }
                if (ctx.consomePrefix() == null) {
                    empty = false;
                    write(ctx, prefix).write(ctx, LT_SLASH_BYTES).write(ctx, name).write(ctx, GT_BYTES);
                }
            }
		} 

		if (empty && ser.isWriteEmpty()){
		    write(ctx, processComments(ctx, prefix, comments)).write(ctx, LT_BYTES).write(ctx, name).write(ctx, SLASH_GT_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeIterator(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Iterator, java.lang.String, org.localmatters.serializer.serialization.Serialization, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	public void writeIterator(Serialization ser, 
			String name,
			Iterator<?> itr, 
			String elementName,
			Serialization element, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(checkRequiredName(ctx, name));
        boolean empty = true;

		String prefix = getPrefix(ctx);
		if (itr.hasNext()) {
		    ctx.pushPrefix(processComments(ctx, prefix, comments) + LT + name + GT);
			while (itr.hasNext()) {
				element.serialize(element, elementName, itr.next(), ctx);
			}
            if (ctx.consomePrefix() == null) {
                empty = false;
                write(ctx, prefix).write(ctx, LT_SLASH_BYTES).write(ctx, name).write(ctx, GT_BYTES);
            }
		} 

        if (empty && ser.isWriteEmpty()){
			write(ctx, processComments(ctx, prefix, comments)).write(ctx, LT_BYTES).write(ctx, name).write(ctx, SLASH_GT_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * @see org.localmatters.serializer.writer.Writer#writeMap(org.localmatters.serializer.serialization.Serialization, java.lang.String, java.util.Collection, java.lang.String, org.localmatters.serializer.serialization.Serialization, java.util.Collection, org.localmatters.serializer.SerializationContext)
	 */
	@SuppressWarnings("rawtypes")
	public void writeMap(Serialization ser, 
			String name,
			Collection<Map.Entry> entries, 
			String key,
			Serialization value, 
			Collection<String> comments, 
			SerializationContext ctx) throws SerializationException {
		ctx.nextLevel(checkRequiredName(ctx, name));
        boolean empty = true;

		String prefix = getPrefix(ctx);
		if (CollectionUtils.isNotEmpty(entries)) {
		    ctx.pushPrefix(processComments(ctx, prefix, comments) + LT + name + GT);
			for (Map.Entry entry : entries) {
				value.serialize(value, resolvesMapKey(key, entry, ctx), entry.getValue(), ctx);
			}
            if (ctx.consomePrefix() == null) {
                empty = false;
                write(ctx, prefix).write(ctx, LT_SLASH_BYTES).write(ctx, name).write(ctx, GT_BYTES);
            }
		} 

        if (empty && ser.isWriteEmpty()){
		    write(ctx, processComments(ctx, prefix, comments)).write(ctx, LT_BYTES).write(ctx, name).write(ctx, SLASH_GT_BYTES);
		}

		ctx.previousLevel();
	}

	/**
	 * Returns the prefix
	 * @param ctx The context
	 * @return The prefix
	 */
	protected String getPrefix(SerializationContext ctx) {
		String prefix = StringUtils.EMPTY;
		int deepness = ctx.getDeepness();
		if (ctx.isFormatting()) {
			prefix = prefixes.get(deepness);
			if (prefix == null) {
				prefix = NEW_LINE;
				for (int i=1; i<deepness; i++) {
					prefix += INDENTATION;
				}
				prefixes.put(deepness, prefix);
			}
		}
		return prefix;
		
	}

	/**
	 * Writes the comments, if any, otherwise, just writes the prefix
	 * @param ctx The context
	 * @param prefix The prefix 
	 * @param comments The comments to write 
	 * @return The writer itself for ease of coding
	 * @throws SerializationException When the writing failed
	 */
	protected String processComments(SerializationContext ctx, String prefix, Collection<String> comments) throws SerializationException {
	    StringBuilder sb = new StringBuilder();
		if (ctx.isFormatting()) {
			if (CollectionUtils.isNotEmpty(comments)) {
				String sep = NEW_LINE + prefix + START_COMMENT; 
				for (String comment : comments) {
					sb.append(sep);
					sb.append(comment.replaceAll(DASH_DASH, STAR_STAR));
					sep = prefix + MIDDLE_COMMENT;
				}
				sb.append(END_COMMENT);
			}
			sb.append(prefix);
		}
		return sb.toString();
	}
}