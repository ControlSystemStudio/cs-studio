package org.csstudio.platform.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for handling strings.
 * 
 * @author <code>splitIgnoreInQuotes</code> by Xihui Chen
 */
public class StringUtil {

	public static final String printArrays(Object value) {
		String result = null;

		if (value == null) {
			result = "null";
		} else if (value instanceof double[]) {
			result = Arrays.toString((double[]) value);
		} else if (value instanceof long[]) {
			result = Arrays.toString((long[]) value);
		} else if (value instanceof String[]) {
			result = Arrays.toString((String[]) value);
		} else if (value instanceof Object[]) {
			result = Arrays.toString((Object[]) value);
		} else {
			result = value.toString();
		}

		return result;
	}

	public static String capitalize(String s) {
		String result = s;
		if (hasLength(s)) {
			result = s.substring(0,1).toUpperCase() + s.substring(1);
		}
		return result;
	}

	public static boolean hasLength(String s) {
		return (s != null && !"".equals(s));
	}

	public static String toSeparatedString(Collection<String> collection, String separator) {
		StringBuffer sb = new StringBuffer();
		
		if(!collection.isEmpty()) {
			Iterator<String> it = collection.iterator();
			sb.append(it.next());
			
			while(it.hasNext()) {
				sb.append(separator);
				sb.append(it.next());
			}
		}
		
		return sb.toString();
	}

	public static String trimNull(String s) {
		return hasLength(s)?s:"";
	}
	
	private static final char SPACE = ' ';
	private static final char QUOTE = '"';

	/**
	 * Split source string into an array of elements by the splitting character,
	 * but the split characters between two quotes will be ignored.
	 * 
	 * @param source
	 *            string to be split
	 * @param splitChar
	 *            the character used to split the source string
	 * @param deleteHeadTailQuotes
	 *            delete the quotes in the head and tail of individual elements
	 *            if true
	 * @return an array of individual elements
	 * @throws Exception
	 *             Exception on parse error (missing end of quoted string)
	 */
	public static String[] splitIgnoreInQuotes(String source, char splitChar, 
			boolean deleteHeadTailQuotes) throws Exception {
		
		// Trim, replace tabs with spaces so we only need to handle
        // space in the following
		source = source.replace('\t', SPACE).trim();
		final List<String> resultList = new ArrayList<String>();
		int pos = 0;
		int start = 0;
		while(pos < source.length()) {
			
			start = pos;
			//skip multiple splitChars
			while(start < source.length() && source.charAt(start) == splitChar)
				start++;
			if(start >= source.length())
				break;
			pos = start;
			
			while(pos < source.length() && source.charAt(pos) !=splitChar) {
				//in case of quote, go to the end of next quote
				if(source.charAt(pos) == QUOTE) {
					final int end = source.indexOf(QUOTE, pos+1);
					if(end < 0)
						throw new Exception("Missing end of quoted text in '" +
								source + "'");
					pos = end + 1;
				} else
					pos++;
			}
			
			String subString = source.substring(start, pos);
			subString = subString.trim();
			if(deleteHeadTailQuotes)
				//only delete quotes when both head and tail are quote
				if(subString.charAt(0) == QUOTE && subString.charAt(subString.length()-1) == QUOTE)
					subString = subString.substring(1, subString.length()-1);
			
			resultList.add(subString);
		}
		return resultList.toArray(new String[resultList.size()]);
	}

}
