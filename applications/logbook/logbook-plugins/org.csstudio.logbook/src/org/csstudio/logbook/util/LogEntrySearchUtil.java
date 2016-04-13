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
 * A utility class to convert back and forth between the log query string into the map of search
 * parameters 
 * 
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
    public static final String SEARCH_KEYWORD_OWNER = Messages.owner;

    public static final String SEARCH_KEYWORD_HISTORY = Messages.history;
    public static final String SEARCH_KEYWORD_PAGE = Messages.page;
    public static final String SEARCH_KEYWORD_COUNT = Messages.count;

    private static final List<String> keywords = new ArrayList<String>(
            Arrays.asList(SEARCH_KEYWORD_TEXT, SEARCH_KEYWORD_LOGBOOKS,
                    SEARCH_KEYWORD_TAGS, SEARCH_KEYWORD_PROPERTIES,
                    SEARCH_KEYWORD_START, SEARCH_KEYWORD_END,
                    SEARCH_KEYWORD_OWNER, SEARCH_KEYWORD_HISTORY,
                    SEARCH_KEYWORD_PAGE, SEARCH_KEYWORD_COUNT));

    /**
     * Convert the search string into a map of search key value parameters
     * 
     * @param search
     * @return search query map
     */
    public static Map<String, String> parseSearchString(String search) {
        return searchParser(search, SEARCH_KEYWORD_TEXT);
    }

    /**
     * Convert the search parameter map into a string
     * TODO shroffk switch to using stream to clean up 
     * 
     * @param searchMap
     * @return search string
     */
    public static String parseSearchMap(Map<String, String> searchMap) {
        StringBuffer search = new StringBuffer();
        for (String keyword : keywords) {
            if (searchMap.containsKey(keyword)
                    && !searchMap.get(keyword).isEmpty()) {
                if (!keyword.equals(SEARCH_KEYWORD_TEXT)) {
                    search.append(keyword);
                    search.append("=");
                }
                search.append(searchMap.get(keyword));
                search.append(" ");
            }
        }
        return search.toString();
    }
}
