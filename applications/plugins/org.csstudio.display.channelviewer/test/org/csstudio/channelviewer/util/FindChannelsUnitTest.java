package org.csstudio.channelviewer.util;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.csstudio.channelviewer.util.FindChannels;
import org.junit.Test;


public class FindChannelsUnitTest {

	@Test
	public void testBuildSearchMap(){
		// everything
		String searchString = "*";
		Map<String, String> result = FindChannels.buildSearchMap(searchString);
		assertTrue("Name", result.get("~name").equals("*"));
		// name, property and tag absolute names
		searchString = "name prop=val tags=tagName";
		result = FindChannels.buildSearchMap(searchString);
		assertTrue("NamePattern", result.get("~name").equals("name"));
		assertTrue("PropertyPattern", result.get("prop").equals("val"));
		assertTrue("TagPattern", result.get("~tag").equals("tagName"));
		
	}
	
	@Test
	public void checkParsingOfORedValues(){
		// check the 2 ways of specifying multiple values
		String searchString = "name propA=val1||val2 propB=val1,val2 tags=tagName";
		Map<String, String> result = FindChannels.buildSearchMap(searchString);
		assertTrue("NamePattern", result.get("~name").equals("name"));
		assertTrue("PropertyPattern", result.get("propA").equals("val1,val2"));
		assertTrue("PropertyPattern", result.get("propB").equals("val1,val2"));
		assertTrue("TagPattern", result.get("~tag").equals("tagName"));
	}
}
