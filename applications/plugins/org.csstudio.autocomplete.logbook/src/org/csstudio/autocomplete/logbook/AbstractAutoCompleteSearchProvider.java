/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Collections;
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
public abstract class AbstractAutoCompleteSearchProvider implements
	IAutoCompleteProvider {

    // The keys represent the supported keywords and the values represent
    // possible values
    private final Map<String, List<String>> keyValueMap;

    public AbstractAutoCompleteSearchProvider() {
	this.keyValueMap = Collections.unmodifiableMap(initializeKeyValueMap());
    }

    /**
     * Configure the KeyValueMap to be used to provide the search proposals.
     * 
     * @return Map<String, List<String>> where the keys are the search Keywords
     *         and the values are the list of possible values
     */
    abstract Map<String, List<String>> initializeKeyValueMap();

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
		Set<String> proposedValues = new LinkedHashSet<String>(
			includedValues);
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
    }

}
