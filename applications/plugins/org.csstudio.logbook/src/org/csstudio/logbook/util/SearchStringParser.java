package org.csstudio.logbook.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class primary to parse a search string of the form
 * 
 * some text keyword:value1,value2 anotherKeyword: value1, value2
 * 
 * @author shroffk
 * 
 */
public class SearchStringParser {

    private SearchStringParser() {
    }

    /**
     * Given a search string of the form
     * 
     * some space separated text keyword1:valueA, ValueB keyword2: 1234, a b c
     * d, xyz
     * 
     * returns a map with the keys mapping to the keywords (the default one
     * being "text") and the value consist of a single string represent
     * 
     * TODO re-evalute the regular expression.
     * 
     * @param string
     * @return
     */
    public static Map<String, String> searchParser(String string,
	    String DefaultKey) {
	Map<String, String> result = new HashMap<String, String>();
	Pattern p = Pattern.compile("([\\S]+):[.]*");
	Matcher m = p.matcher(string);
	int start = 0;
	int end;
	String key = DefaultKey;
	while (m.find()) {
	    end = (m.start() - 1) >= 0 ? (m.start() - 1) : 0;
	    result.put(key, string.substring(start, end).trim());
	    key = m.group(1);
	    start = m.end(1) + 1;
	}
	String value = string.substring(start, string.length()).trim();
	if (value != null && !value.isEmpty())
		result.put(key, value);
	return result;
    }
}
