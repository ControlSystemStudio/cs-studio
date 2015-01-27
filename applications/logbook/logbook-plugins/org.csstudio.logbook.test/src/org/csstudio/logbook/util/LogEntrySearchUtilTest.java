/**
 * 
 */
package org.csstudio.logbook.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import static org.csstudio.logbook.util.LogEntrySearchUtil.parseSearchString;

/**
 * @author shroffk
 * 
 */
public class LogEntrySearchUtilTest {

    @Test
    public void test() {
	String searchString = "";
	Map<String, String> expectedSearchMap = new HashMap<String, String>();
	searchString = "Hello";
	expectedSearchMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_TEXT, "Hello");
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("logbooks", "Operation");
	searchString = "Hello logbooks:Operation";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("tags", "LOTO");
	searchString = "Hello logbooks:Operation tags:LOTO";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("shift.Id", "1234");
	searchString = "Hello logbooks:Operation tags:LOTO shift.Id:1234";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("from", "lastday");
	searchString = "Hello logbooks:Operation tags:LOTO shift.Id:1234 from:lastday";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("from", "last5days");
	searchString = "Hello logbooks:Operation tags:LOTO shift.Id:1234 from:last5days";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));
	expectedSearchMap.put("to", "now");
	searchString = "Hello logbooks:Operation tags:LOTO shift.Id:1234 from:last5days to:now";
	Assert.assertEquals("Failed to parse search String: " + searchString,
		expectedSearchMap, parseSearchString(searchString));

    }

}
