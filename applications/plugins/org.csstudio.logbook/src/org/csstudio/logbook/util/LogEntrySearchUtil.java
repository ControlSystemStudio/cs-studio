/**
 * 
 */
package org.csstudio.logbook.util;

import static org.csstudio.logbook.util.SearchStringParser.searchParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	Map<String, String> searchMap = searchParser(search,
		SEARCH_KEYWORD_TEXT);
	if (keywords.containsAll(searchMap.keySet())) {
	    return searchMap;
	} else {
	    throw new IllegalArgumentException("Search string:" + search
		    + " has an invalid keyword");
	}
    }
}
