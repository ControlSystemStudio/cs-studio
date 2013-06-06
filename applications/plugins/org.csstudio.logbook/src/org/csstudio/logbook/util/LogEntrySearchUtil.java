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

    public static final String SEARCH_KEYWORD_TEXT = Messages.search;
    public static final String SEARCH_KEYWORD_LOGBOOKS = Messages.logbook;
    public static final String SEARCH_KEYWORD_TAGS = Messages.tag;
    public static final String SEARCH_KEYWORD_PROPERTIES = Messages.properties;
    public static final String SEARCH_KEYWORD_START = Messages.from;
    public static final String SEARCH_KEYWORD_END = Messages.to;

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
	    throw new IllegalArgumentException("Search string:" + search //$NON-NLS-1$
		    + " has an invalid keyword"); //$NON-NLS-1$
	}
    }

    public static String parseSearchMap(Map<String, String> searchMap) {
	StringBuffer search = new StringBuffer();
	for (String keyword : keywords) {
	    if (searchMap.containsKey(keyword)) {
		if (!keyword.equals(SEARCH_KEYWORD_TEXT)) {
		    search.append(keyword);
		    search.append(":");
		}
		search.append(searchMap.get(keyword));
		search.append(" ");
	    }
	}
	return search.toString();
    }
}
