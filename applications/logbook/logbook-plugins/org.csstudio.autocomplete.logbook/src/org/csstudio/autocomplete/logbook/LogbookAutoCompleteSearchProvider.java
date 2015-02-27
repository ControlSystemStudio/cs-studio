/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.util.LogEntrySearchUtil;

/**
 * @author shroffk
 * 
 */
public class LogbookAutoCompleteSearchProvider extends
	AbstractAutoCompleteSearchProvider {

    @Override
    Map<String, List<String>> initializeKeyValueMap() {
	try {
	    Map<String, List<String>> keyValueMap = new HashMap<String, List<String>>();
	    LogbookClient logbookClient = LogbookClientManager
		    .getLogbookClientFactory().getClient();
	    List<String> logbooks = new ArrayList<String>();
	    for (Logbook logbook : logbookClient.listLogbooks()) {
		logbooks.add(logbook.getName());
	    }
	    List<String> tags = new ArrayList<String>();
	    for (Tag tag : logbookClient.listTags()) {
		tags.add(tag.getName());
	    }
	    List<String> properties = new ArrayList<String>();
	    for (Property property : logbookClient.listProperties()) {
		properties.add(property.getName());
	    }
	    List<String> timeOptions = new ArrayList<String>(Arrays.asList(
		    "lastMin", "1minAgo", "lastHour", "1hourAgo", "lastDay",
		    "1dayAgo", "lastWeek", "1weekAgo"));
	    keyValueMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_LOGBOOKS,
		    logbooks);
	    keyValueMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_TAGS, tags);
	    keyValueMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_PROPERTIES,
		    properties);
	    keyValueMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_START,
		    timeOptions);
	    keyValueMap.put(LogEntrySearchUtil.SEARCH_KEYWORD_END, timeOptions);
	    return keyValueMap;
	} catch (Exception e1) {
	    return Collections.emptyMap();
	}

    }
}
