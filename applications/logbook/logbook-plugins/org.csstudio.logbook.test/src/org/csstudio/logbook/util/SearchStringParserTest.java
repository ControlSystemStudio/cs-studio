/**
 *
 */
package org.csstudio.logbook.util;

import static org.csstudio.logbook.util.SearchStringParser.searchParser;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author shroffk
 *
 */
public class SearchStringParserTest {
    private static final String defaultKey = "text";

    @Test
    public void test() {
    String simpleText = "test";
    Map<String, String> expectedSimpleText = new HashMap<String, String>();
    expectedSimpleText.put("text", "test");
    assertEquals("Failed to parse " + simpleText, expectedSimpleText,
        searchParser(simpleText, defaultKey));

    Map<String, String> expectedText = new HashMap<String, String>();
    expectedText.put("text", "test with space");
    assertEquals("Failed to parse " + simpleText, expectedSimpleText,
        searchParser(simpleText, defaultKey));

    String textAndlogbook = "test logbook:Operations";
    Map<String, String> expectedTextAndlogbook = new HashMap<String, String>();
    expectedTextAndlogbook.put("text", "test");
    expectedTextAndlogbook.put("logbook", "Operations");
    assertEquals("Failed to parse " + textAndlogbook,
        expectedTextAndlogbook,
        searchParser(textAndlogbook, defaultKey));

    String textAndProperty = "test shift.Id:1";
    Map<String, String> expectedTextAndProperty = new HashMap<String, String>();
    expectedTextAndProperty.put("text", "test");
    expectedTextAndProperty.put("shift.Id", "1");
    assertEquals("Failed to parse " + textAndProperty,
        expectedTextAndProperty,
        searchParser(textAndProperty, defaultKey));

    String textAnd2Property = "test shift.Id:1 SignOff.signature:test";
    Map<String, String> expectedTextAnd2Property = new HashMap<String, String>();
    expectedTextAnd2Property.put("text", "test");
    expectedTextAnd2Property.put("shift.Id", "1");
    expectedTextAnd2Property.put("SignOff.signature", "test");
    assertEquals("Failed to parse " + textAnd2Property,
        expectedTextAnd2Property,
        searchParser(textAnd2Property, defaultKey));

    String textAndlogbookAndTags = "test logbook:Operations tags:testTag";
    Map<String, String> expectedTextAndlogbookAndTags = new HashMap<String, String>();
    expectedTextAndlogbookAndTags.put("text", "test");
    expectedTextAndlogbookAndTags.put("logbook", "Operations");
    expectedTextAndlogbookAndTags.put("tags", "testTag");
    assertEquals("Failed to parse " + textAndlogbookAndTags,
        expectedTextAndlogbookAndTags,
        searchParser(textAndlogbookAndTags, defaultKey));

    String textAndlogbookAndTagsAndFrom = "test logbook:Operations tags:testTag from:-5 days";
    Map<String, String> expectedTextAndlogbookAndTagsAndFrom = new HashMap<String, String>();
    expectedTextAndlogbookAndTagsAndFrom.put("text", "test");
    expectedTextAndlogbookAndTagsAndFrom.put("logbook", "Operations");
    expectedTextAndlogbookAndTagsAndFrom.put("tags", "testTag");
    expectedTextAndlogbookAndTagsAndFrom.put("from", "-5 days");
    assertEquals("Failed to parse " + textAndlogbookAndTagsAndFrom,
        expectedTextAndlogbookAndTagsAndFrom,
        searchParser(textAndlogbookAndTagsAndFrom, defaultKey));

    String textAndlogbookAndTagsAndFrom2 = "test logbook:Operations tags:testTag from:-5 days";
    Map<String, String> expectedTextAndlogbookAndTagsAndFrom2 = new HashMap<String, String>();
    expectedTextAndlogbookAndTagsAndFrom2.put("text", "test");
    expectedTextAndlogbookAndTagsAndFrom2.put("logbook", "Operations");
    expectedTextAndlogbookAndTagsAndFrom2.put("tags", "testTag");
    expectedTextAndlogbookAndTagsAndFrom2.put("from", "-5 days");
    assertEquals("Failed to parse " + textAndlogbookAndTagsAndFrom2,
        expectedTextAndlogbookAndTagsAndFrom2,
        searchParser(textAndlogbookAndTagsAndFrom2, defaultKey));

    String textAndlogbookAndTagsAndRange = "test logbook:Operations tags:testTag from:-5 days to:now";
    Map<String, String> expectedTextAndlogbookAndTagsAndRange = new HashMap<String, String>();
    expectedTextAndlogbookAndTagsAndRange.put("text", "test");
    expectedTextAndlogbookAndTagsAndRange.put("logbook", "Operations");
    expectedTextAndlogbookAndTagsAndRange.put("tags", "testTag");
    expectedTextAndlogbookAndTagsAndRange.put("from", "-5 days");
    expectedTextAndlogbookAndTagsAndRange.put("to", "now");
    assertEquals("Failed to parse " + textAndlogbookAndTagsAndRange,
        expectedTextAndlogbookAndTagsAndRange,
        searchParser(textAndlogbookAndTagsAndRange, defaultKey));

    String search = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:-5 days to: now";
    Map<String, String> expectedSearch = new HashMap<String, String>();
    expectedSearch.put("text", "search for some text");
    expectedSearch.put("logbook", "Operations, LOTO,Commissioning");
    expectedSearch.put("tags", "testTag, testTag2");
    expectedSearch.put("from", "-5 days");
    expectedSearch.put("to", "now");
    assertEquals("Failed to parse " + search, expectedSearch,
        searchParser(search, defaultKey));

    // Test for the new absolute time format associated with the java time
    String searchAbsoluteTime = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:2016/04/12 14:11:00.000 to: now";
    Map<String, String> expectedSearchAbsoluteTime = new HashMap<String, String>();
    expectedSearchAbsoluteTime.put("text", "search for some text");
    expectedSearchAbsoluteTime.put("logbook", "Operations, LOTO,Commissioning");
    expectedSearchAbsoluteTime.put("tags", "testTag, testTag2");
    expectedSearchAbsoluteTime.put("from", "2016/04/12 14:11:00.000");
    expectedSearchAbsoluteTime.put("to", "now");
    assertEquals("Failed to parse " + searchAbsoluteTime, expectedSearchAbsoluteTime,
        searchParser(searchAbsoluteTime, defaultKey));

    searchAbsoluteTime = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:2016/04/12 14:11:00.000 to: 2016/04/12 15:11";
    expectedSearchAbsoluteTime = new HashMap<String, String>();
    expectedSearchAbsoluteTime.put("text", "search for some text");
    expectedSearchAbsoluteTime.put("logbook", "Operations, LOTO,Commissioning");
    expectedSearchAbsoluteTime.put("tags", "testTag, testTag2");
    expectedSearchAbsoluteTime.put("from", "2016/04/12 14:11:00.000");
    expectedSearchAbsoluteTime.put("to", "2016/04/12 15:11");
    assertEquals("Failed to parse " + searchAbsoluteTime, expectedSearchAbsoluteTime,
        searchParser(searchAbsoluteTime, defaultKey));

    searchAbsoluteTime = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:14:11 to:15:11";
    expectedSearchAbsoluteTime = new HashMap<String, String>();
    expectedSearchAbsoluteTime.put("text", "search for some text");
    expectedSearchAbsoluteTime.put("logbook", "Operations, LOTO,Commissioning");
    expectedSearchAbsoluteTime.put("tags", "testTag, testTag2");
    expectedSearchAbsoluteTime.put("from", "14:11");
    expectedSearchAbsoluteTime.put("to", "15:11");
    assertEquals("Failed to parse " + searchAbsoluteTime, expectedSearchAbsoluteTime,
        searchParser(searchAbsoluteTime, defaultKey));

    String searchRelativeTime = "search for some text logbook:Operations, LOTO,Commissioning tags: testTag, testTag2 from:-5 days 0.0 seconds to: now";
    Map<String, String> expectedRelativeAbsoluteTime = new HashMap<String, String>();
    expectedRelativeAbsoluteTime.put("text", "search for some text");
    expectedRelativeAbsoluteTime.put("logbook", "Operations, LOTO,Commissioning");
    expectedRelativeAbsoluteTime.put("tags", "testTag, testTag2");
    expectedRelativeAbsoluteTime.put("from", "-5 days 0.0 seconds");
    expectedRelativeAbsoluteTime.put("to", "now");
    assertEquals("Failed to parse " + searchRelativeTime, expectedRelativeAbsoluteTime,
        searchParser(searchRelativeTime, defaultKey));

    }

}
