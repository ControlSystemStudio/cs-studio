package org.csstudio.apputil.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for splitting string into elements
 * @author Xihui Chen
 *
 */
public class StringSplitter {
	
	 private static final char SPACE = ' ';
	private static final char QUOTE = '"';
	
	/**
	 * Split source string into an array of elements by the splitting character, 
	 * but the split characters between two quotes will be ignored.
	 * @param source string to be split
	 * @param splitChar the character used to split the source string
	 * @param deleteHeadTailQuotes delete the quotes in head and tail if true
	 * @return an array of individual elements
	 * @throws Exception Exception on parse error (missing end of quoted string)
	 */
	public static String[] splitIgnoreInQuotes(String source, char splitChar, boolean deleteHeadTailQuotes) throws Exception {
		
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
			if(deleteHeadTailQuotes)
				//only delete quotes when both head and tail are quote
				if(subString.charAt(0) == QUOTE && subString.charAt(subString.length()-1) == QUOTE)
					subString = subString.substring(1, subString.length()-1);
			
			resultList.add(subString);				
		}		
		return resultList.toArray(new String[resultList.size()]);
	}	

	
}
