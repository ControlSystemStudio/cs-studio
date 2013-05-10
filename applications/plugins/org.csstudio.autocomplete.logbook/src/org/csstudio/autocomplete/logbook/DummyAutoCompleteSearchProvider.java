/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		"logbooks",
		new ArrayList<String>(Arrays.asList("Operations", "Test",
			"Controls")));
	result.put("tags",
		new ArrayList<String>(Arrays.asList("Timming", "Bumps", "RF")));
	return result;
    }

}
