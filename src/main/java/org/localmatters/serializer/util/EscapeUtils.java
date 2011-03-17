package org.localmatters.serializer.util;

import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Class to escape strings
 */
public class EscapeUtils extends StringEscapeUtils {

	/**
     * Escapes the characters in a <code>String</code> using JSON String rules.
     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, 
     * etc.). So a tab becomes the characters <code>'\\'</code> and 
     * <code>'t'</code>.
     * @param str The string to escape
     * @return The escaped string
     */
    public static String escapeJson(String str) {
        if (str == null) {
            return null;
        }
        StringWriter writer = new StringWriter(str.length() * 2);
        int sz;
        sz = str.length();
        for (int i=0; i<sz; i++) {
        	char ch = str.charAt(i);
    		switch (ch) {
    		case '\b':
    			writer.write('\\');
    			writer.write('b');
    			break;
    		case '\n':
    			writer.write('\\');
    			writer.write('n');
    			break;
    		case '\t':
    			writer.write('\\');
    			writer.write('t');
    			break;
    		case '\f':
    			writer.write('\\');
    			writer.write('f');
    			break;
    		case '\r':
    			writer.write('\\');
    			writer.write('r');
    			break;
            case '"':
            	writer.write('\\');
            	writer.write('"');
                break;
            case '\\':
            	writer.write('\\');
            	writer.write('\\');
                break;
    		default :
    			writer.write(ch);
    			break;
    		}
        }
        return writer.toString();
    }
}
