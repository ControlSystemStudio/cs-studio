/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;

import com.google.common.base.Joiner;

/**
 * @author shroffk
 * 
 */
public class AutoCompleteSearchProvider implements IAutoCompleteProvider {

    // The keys represent the supported keywords and the values represent
    // possible values
    private Map<String, List<String>> keyValueMap = new HashMap<String, List<String>>();

    public AutoCompleteSearchProvider() {
	// TODO Auto-generated constructor stub
	System.out.println("creating");
	keyValueMap.put(
		"logbooks",
		new ArrayList<String>(Arrays.asList("Operations",
			"Commissioning", "Deployment")));
	keyValueMap.put(
		"tags",
		new ArrayList<String>(Arrays
			.asList("LOTO", "Timing", "shroffk")));
    }

    @Override
    public AutoCompleteResult listResult(String type, String name, int limit) {
	AutoCompleteResult result = new AutoCompleteResult();
	name = name.trim();
	String fixedFirstPart = name.substring(0,
		name.lastIndexOf(' ') > 0 ? name.lastIndexOf(' ') : 0);
	String lastPart = name.substring(name.lastIndexOf(' ') + 1);
	final String[] keyValue = lastPart.split(":");
	if (keyValue.length == 1) {
	    // search for possible matches to key words
	    for (String key : keyValueMap.keySet()) {
		if (key.startsWith(keyValue[0].substring(0,
			keyValue[0].length() - 1))) {
		    result.add(fixedFirstPart + ' ' + key + ":");
		    result.setCount(result.getCount() + 1);
		}
	    }
	} else if (keyValue.length == 2) {
	    // search for possible matches for the values
	    String key = keyValue[0];
	    String valuePattern;
	    Set<String> includedValues = new LinkedHashSet<String>();
	    if (keyValue[1].contains(",")) {
		includedValues.addAll(Arrays.asList(keyValue[1].substring(0,
			keyValue[1].lastIndexOf(',')).split(",")));
		valuePattern = keyValue[1].substring(
			keyValue[1].lastIndexOf(',') + 1,
			keyValue[1].length() - 1).trim();
	    } else {
		valuePattern = keyValue[1].substring(0,
			keyValue[1].length() - 1);
	    }
	    for (String value : keyValueMap.get(key)) {
		Set<String> proposedValues = new LinkedHashSet<String>(includedValues);
		if (value.startsWith(valuePattern)) {
		    proposedValues.add(value);
		    result.add(fixedFirstPart + ' ' + key + ':'
			    + Joiner.on(',').join(proposedValues));
		    result.setCount(result.getCount() + 1);
		}
	    }
	} else {
	    //
	}
	return result;
    }

    @Override
    public void cancel() {
	// TODO Auto-generated method stub

    }

}
