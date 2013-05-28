package org.csstudio.autocomplete.logbook;

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
     * Returns the last comparable piece.
     * 
     * For a plain sting return the entire string with the spaces included
     * 
     * If there are keywords present identified by a ':' it returns the entire
     * string following the colon
     * 
     * If there are keywords present identified by a ':' with multiple values
     * separated with a comma, then the string following the last comma is
     * returned
     * 
     * @param searchString
     * @return
     */
    public static String lastComparablePiece(String searchString) {
	String result = searchString;
	if (result.contains(":")) {
	    result = result.substring(result.lastIndexOf(":") + 1);
	    if (result.contains(",")) {
		result = result.substring(result.lastIndexOf(",") + 1);
	    }
	} else {
	    result = result.substring(result.lastIndexOf(' ') + 1);
	}
	return result;
    }

    /**
     * Simply returns the last space separated word in the given string
     * 
     * @param searchString
     * @return
     */
    public static String lastSpaceSeperatedPiece(String searchString) {
	String result = searchString;
	result = result.substring(result.lastIndexOf(' ') + 1);
	return result;
    }

    /**
     * Given a search string of the form
     * 
     * some space separated text keyword1:valueA, ValueB keyword2: 1234, a b c d,
     * xyz
     * 
     * returns a map with the keys mapping to the keywords (the default one
     * being "text") and the value consist of a single string represent
     * 
     * TODO re-evalute the regular expression.
     * 
     * @param string
     * @return
     */
    public static Map<String, String> searchParser(String string) {
	Map<String, String> result = new HashMap<String, String>();
	Pattern p = Pattern.compile("([\\S]*):[.]*");
	Matcher m = p.matcher(string);
	int start = 0;
	int end;
	String key = "text";
	while (m.find()) {
	    end = m.start() - 1;
	    result.put(key, string.substring(start, end).trim());
	    key = m.group(1);
	    start = m.end(1) + 1;
	}
	result.put(key, string.substring(start, string.length()).trim());
	return result;
    }
}
