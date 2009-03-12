package org.csstudio.apputil.text;

import org.csstudio.platform.util.StringUtil;

/**
 * Helper for splitting string into elements
 * @author Xihui Chen
 *
 */
public class StringSplitter {
	
	/**
	 * Split source string into an array of elements by the splitting character, 
	 * but the split characters between two quotes will be ignored.
	 * @param source string to be split
	 * @param splitChar the character used to split the source string
	 * @param deleteHeadTailQuotes delete the quotes in the head and tail
	 * of individual elements if true
	 * @return an array of individual elements
	 * @throws Exception Exception on parse error (missing end of quoted string)
	 * @deprecated use {@link StringUtil#splitIgnoreInQuotes} instead.
	 */
	public static String[] splitIgnoreInQuotes(String source, char splitChar, 
			boolean deleteHeadTailQuotes) throws Exception {
		return StringUtil.splitIgnoreInQuotes(source, splitChar, deleteHeadTailQuotes);
	}	

	
}
