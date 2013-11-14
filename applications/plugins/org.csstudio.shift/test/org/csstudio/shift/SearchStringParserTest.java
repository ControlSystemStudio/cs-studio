/**
 * 
 */
package org.csstudio.shift;

import static org.csstudio.shift.util.SearchStringParser.searchParser;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class SearchStringParserTest {
    private static final String defaultKey = "text";

    @Test
    public void test() {
	String simpleText = "test";
	Map<String, String> expectedSimpleText = new HashMap<String, String>();
	expectedSimpleText.put("text", "test");
	assertEquals("Failed to parse " + simpleText, expectedSimpleText,
		searchParser(simpleText, defaultKey));

	String text = "test with space";
	Map<String, String> expectedText = new HashMap<String, String>();
	expectedText.put("text", "test with space");
	assertEquals("Failed to parse " + simpleText, expectedSimpleText,
		searchParser(simpleText, defaultKey));

	String textAndShift = "test shift:12";
	Map<String, String> expectedTextAndShift = new HashMap<String, String>();
	expectedTextAndShift.put("text", "test");
	expectedTextAndShift.put("shift", "12");
	assertEquals("Failed to parse " + textAndShift,
		expectedTextAndShift,
		searchParser(textAndShift, defaultKey));


	String textAndShiftAndFrom = "test shift:12 from:5 days ago";
	Map<String, String> expectedTextAndShiftAndFrom = new HashMap<String, String>();
	expectedTextAndShiftAndFrom.put("text", "test");
	expectedTextAndShiftAndFrom.put("shift", "12");
	expectedTextAndShiftAndFrom.put("from", "5 days ago");
	assertEquals("Failed to parse " + textAndShiftAndFrom,
		expectedTextAndShiftAndFrom,
		searchParser(textAndShiftAndFrom, defaultKey));

	String textAndShiftAndFrom2 = "test shift:12 from:last 5 days";
	Map<String, String> expectedTextAndShiftAndFrom2 = new HashMap<String, String>();
	expectedTextAndShiftAndFrom2.put("text", "test");
	expectedTextAndShiftAndFrom2.put("shift", "12");
	expectedTextAndShiftAndFrom2.put("from", "last 5 days");
	assertEquals("Failed to parse " + textAndShiftAndFrom2,
		expectedTextAndShiftAndFrom2,
		searchParser(textAndShiftAndFrom2, defaultKey));

	String textAndShiftAndRange = "test shift:12 from:last 5 days to:now";
	Map<String, String> expectedTextAndShiftAndRange = new HashMap<String, String>();
	expectedTextAndShiftAndRange.put("text", "test");
	expectedTextAndShiftAndRange.put("shift", "12");
	expectedTextAndShiftAndRange.put("from", "last 5 days");
	expectedTextAndShiftAndRange.put("to", "now");
	assertEquals("Failed to parse " + textAndShiftAndRange,
		expectedTextAndShiftAndRange,
		searchParser(textAndShiftAndRange, defaultKey));

	String search = "search for some text shift:12, 13, 14 from:last 5 days to: now";
	Map<String, String> expectedSearch = new HashMap<String, String>();
	expectedSearch.put("text", "search for some text");
	expectedSearch.put("shift", "12, 13, 14");
	expectedSearch.put("from", "last 5 days");
	expectedSearch.put("to", "now");
	assertEquals("Failed to parse " + search, expectedSearch,
		searchParser(search, defaultKey));
    }

}
