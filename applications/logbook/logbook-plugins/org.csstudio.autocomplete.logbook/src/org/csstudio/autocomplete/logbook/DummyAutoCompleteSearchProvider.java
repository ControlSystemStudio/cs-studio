/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.logbook.util.LogEntrySearchUtil;

/**
 * @author shroffk
 * 
 */
public class DummyAutoCompleteSearchProvider extends
	AbstractAutoCompleteSearchProvider {

    @Override
    Map<String, List<String>> initializeKeyValueMap() {
	Map<String, List<String>> result = new HashMap<String, List<String>>();
	result.put(
		LogEntrySearchUtil.SEARCH_KEYWORD_LOGBOOKS,
		new ArrayList<String>(Arrays.asList("Operations", "Test",
			"Controls")));
	result.put(LogEntrySearchUtil.SEARCH_KEYWORD_TAGS,
		new ArrayList<String>(Arrays.asList("Timing", "Bumps", "RF")));
	return result;
    }

}
