/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import static org.junit.Assert.*;
import static org.csstudio.autocomplete.logbook.SearchStringParser.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * @author shroffk
 * 
 */
public class SearchStringParserTest {

    @Test
    public void test() {
	String simpleText = "test";
	Map<String, String> expectedSimpleText = new HashMap<String, String>();
	expectedSimpleText.put("text", "test");
	assertEquals("Failed to parse " + simpleText, expectedSimpleText,
		searchParser(simpleText));

	String text = "test with space";
	Map<String, String> expectedText = new HashMap<String, String>();
	expectedText.put("text", "test with space");
	searchParser(text);
	assertEquals("Failed to parse " + simpleText, expectedSimpleText,
		searchParser(simpleText));

	String textAndlogbook = "test logbook:Operations";
	Map<String, String> expectedTextAndlogbook = new HashMap<String, String>();
	expectedTextAndlogbook.put("text", "test");
	expectedTextAndlogbook.put("logbook", "Operations");
	assertEquals("Failed to parse " + textAndlogbook,
		expectedTextAndlogbook, searchParser(textAndlogbook));

	String textAndlogbookAndTags = "test logbook:Operations tags:testTag";
	Map<String, String> expectedTextAndlogbookAndTags = new HashMap<String, String>();
	expectedTextAndlogbookAndTags.put("text", "test");
	expectedTextAndlogbookAndTags.put("logbook", "Operations");
	expectedTextAndlogbookAndTags.put("tags", "testTag");
	assertEquals("Failed to parse " + textAndlogbookAndTags,
		expectedTextAndlogbookAndTags,
		searchParser(textAndlogbookAndTags));

	String textAndlogbookAndTagsAndFrom = "test logbook:Operations tags:testTag from:5 days ago";
	Map<String, String> expectedTextAndlogbookAndTagsAndFrom = new HashMap<String, String>();
	expectedTextAndlogbookAndTagsAndFrom.put("text", "test");
	expectedTextAndlogbookAndTagsAndFrom.put("logbook", "Operations");
	expectedTextAndlogbookAndTagsAndFrom.put("tags", "testTag");
	expectedTextAndlogbookAndTagsAndFrom.put("from", "5 days ago");
	assertEquals("Failed to parse " + textAndlogbookAndTagsAndFrom,
		expectedTextAndlogbookAndTagsAndFrom,
		searchParser(textAndlogbookAndTagsAndFrom));

	String textAndlogbookAndTagsAndFrom2 = "test logbook:Operations tags:testTag from:last 5 days";
	Map<String, String> expectedTextAndlogbookAndTagsAndFrom2 = new HashMap<String, String>();
	expectedTextAndlogbookAndTagsAndFrom2.put("text", "test");
	expectedTextAndlogbookAndTagsAndFrom2.put("logbook", "Operations");
	expectedTextAndlogbookAndTagsAndFrom2.put("tags", "testTag");
	expectedTextAndlogbookAndTagsAndFrom2.put("from", "last 5 days");
	assertEquals("Failed to parse " + textAndlogbookAndTagsAndFrom2,
		expectedTextAndlogbookAndTagsAndFrom2,
		searchParser(textAndlogbookAndTagsAndFrom2));

	String textAndlogbookAndTagsAndRange = "test logbook:Operations tags:testTag from:last 5 days to:now";
	Map<String, String> expectedTextAndlogbookAndTagsAndRange = new HashMap<String, String>();
	expectedTextAndlogbookAndTagsAndRange.put("text", "test");
	expectedTextAndlogbookAndTagsAndRange.put("logbook", "Operations");
	expectedTextAndlogbookAndTagsAndRange.put("tags", "testTag");
	expectedTextAndlogbookAndTagsAndRange.put("from", "last 5 days");
	expectedTextAndlogbookAndTagsAndRange.put("to", "now");
	assertEquals("Failed to parse " + textAndlogbookAndTagsAndRange,
		expectedTextAndlogbookAndTagsAndRange,
		searchParser(textAndlogbookAndTagsAndRange));

	String search = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:last 5 days to: now";
	Map<String, String> expectedSearch = new HashMap<String, String>();
	expectedSearch.put("text", "search for some text");
	expectedSearch.put("logbook", "Operations, LOTO,Commissioning");
	expectedSearch.put("tags", "testTag, testTag2");
	expectedSearch.put("from", "last 5 days");
	expectedSearch.put("to", "now");
	assertEquals("Failed to parse " + search, expectedSearch,
		searchParser(search));
    }

}
