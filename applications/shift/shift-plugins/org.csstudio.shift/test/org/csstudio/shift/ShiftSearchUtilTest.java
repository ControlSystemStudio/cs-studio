/**
 * 
 */
package org.csstudio.shift;

import static org.csstudio.shift.util.ShiftSearchUtil.parseSearchString;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.shift.util.ShiftSearchUtil;
import org.junit.Assert;
import org.junit.Test;


public class ShiftSearchUtilTest {

    @Test
    public void test() {
	String searchString = "";
	Map<String, String> expectedSearchMap = new HashMap<String, String>();
	searchString = "Hello";
	expectedSearchMap.put(ShiftSearchUtil.SEARCH_KEYWORD_TEXT, "Hello");
	Assert.assertEquals("Failed to parse search String: " + searchString, expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("shift", "12"); 
	searchString = "Hello shift:12";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("from", "lastday");
	searchString = "Hello shift:12 from:lastday";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("from", "last5days");
	searchString = "Hello shift:12 from:last5days";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("to", "now");
	searchString = "Hello shift:12 from:last5days to:now";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));

    }

}
