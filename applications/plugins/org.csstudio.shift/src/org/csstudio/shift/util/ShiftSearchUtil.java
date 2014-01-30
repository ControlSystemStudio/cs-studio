package org.csstudio.shift.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.csstudio.shift.util.SearchStringParser.searchParser;

public class ShiftSearchUtil {
	public static final String SEARCH_KEYWORD_TEXT = Messages.search;
    public static final String SEARCH_KEYWORD_SHIFTS = Messages.shift;
    public static final String SEARCH_KEYWORD_START = Messages.from;
    public static final String SEARCH_KEYWORD_END = Messages.to;
    public static final String SEARCH_KEYWORD_OWNER = Messages.owner;
    public static final String SEARCH_KEYWORD_LEADOPERATOR = Messages.leadOperator;
    public static final String SEARCH_KEYWORD_CLOSEUSER = Messages.closeUser;
    public static final String SEARCH_KEYWORD_TYPE = Messages.type;
    public static final String SEARCH_KEYWORD_STATUS = Messages.status;




    private static final List<String> keywords = new ArrayList<String>(
	    Arrays.asList(SEARCH_KEYWORD_TEXT, SEARCH_KEYWORD_SHIFTS,
		    SEARCH_KEYWORD_START, SEARCH_KEYWORD_END, SEARCH_KEYWORD_OWNER, SEARCH_KEYWORD_LEADOPERATOR, 
		    SEARCH_KEYWORD_CLOSEUSER, SEARCH_KEYWORD_TYPE, SEARCH_KEYWORD_STATUS));

    public static Map<String, String> parseSearchString(final String search) {
        final Map<String, String> searchMap = searchParser(search, SEARCH_KEYWORD_TEXT);
        if (keywords.containsAll(searchMap.keySet())) {
            return searchMap;
        } else {
            throw new IllegalArgumentException("Search string:" + search //$NON-NLS-1$
                + " has an invalid keyword"); //$NON-NLS-1$
        }
    }

    public static String parseSearchMap(final Map<String, String> searchMap) {
    	//TODO:check if needed multiple variables per search term (example in 2 different owners)
        final StringBuffer search = new StringBuffer();
        for (String keyword : keywords) {
            if (searchMap.containsKey(keyword)
                && !searchMap.get(keyword).isEmpty()) {
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
