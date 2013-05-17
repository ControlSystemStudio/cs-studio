/**
 * 
 */
package org.csstudio.logbook.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xpath.internal.compiler.Keywords;

/**
 * @author shroffk
 * 
 */
public class LogEntrySearchUtil {

    public static final String SEARCH_KEYWORD_TEXT = "search";
    public static final String SEARCH_KEYWORD_LOGBOOKS = "logbooks";
    public static final String SEARCH_KEYWORD_TAGS = "tags";
    public static final String SEARCH_KEYWORD_PROPERTIES = "properties";
    public static final String SEARCH_KEYWORD_START = "from";
    public static final String SEARCH_KEYWORD_END = "to";

    private static final List<String> keywords = new ArrayList<String>(
	    Arrays.asList(SEARCH_KEYWORD_TEXT, SEARCH_KEYWORD_LOGBOOKS,
		    SEARCH_KEYWORD_TAGS, SEARCH_KEYWORD_PROPERTIES,
		    SEARCH_KEYWORD_START, SEARCH_KEYWORD_END));

    public static Map<String, String> parseSearchString(String search) {
	Map<String, String> searchMap = new HashMap<String, String>();
	List<String> list = new ArrayList<String>();
	Matcher regexMatcher = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'").matcher(search);
	while (regexMatcher.find()) {
	    if (regexMatcher.group(1) != null) {
	        // Add double-quoted string without the quotes
		list.add(regexMatcher.group(1));
	    } else if (regexMatcher.group(2) != null) {
	        // Add single-quoted string without the quotes
		list.add(regexMatcher.group(2));
	    } else {
	        // Add unquoted word
		list.add(regexMatcher.group());
	    }
	} 
	for (String searchParameter : list) {
	    if (searchParameter.contains(":")) {
		String key = searchParameter.split(":")[0];
		String value = searchParameter.split(":")[1];
		if (keywords.contains(key)) {
		    searchMap.put(key, value);
		}
	    } else {
		searchMap.put(SEARCH_KEYWORD_TEXT, searchParameter);
	    }
	}
	return searchMap;
    }
}
