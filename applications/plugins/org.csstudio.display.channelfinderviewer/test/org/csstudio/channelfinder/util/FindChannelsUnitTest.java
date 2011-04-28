package org.csstudio.channelfinder.util;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;


public class FindChannelsUnitTest {

	@Test
	public void testBuildSearchMap(){
		String searchString = "*";
		Map<String, String> result = FindChannels.buildSearchMap(searchString);
		assertTrue("Name", result.get("~name").equals("*"));
		searchString = "name prop=val tags=tagName";
		result = FindChannels.buildSearchMap(searchString);
		assertTrue("NamePattern", result.get("~name").equals("name"));
		assertTrue("PropertyPattern", result.get("prop").equals("val"));
		assertTrue("TagPattern", result.get("~tag").equals("tagName"));
	}
}
