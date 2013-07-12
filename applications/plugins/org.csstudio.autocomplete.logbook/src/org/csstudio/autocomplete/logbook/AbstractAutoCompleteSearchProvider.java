/**
 * 
 */
package org.csstudio.autocomplete.logbook;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private Map<String, List<String>> keyValueMap;

    AbstractAutoCompleteSearchProvider() {

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
	if (keyValueMap == null) {
	    keyValueMap = Collections.unmodifiableMap(initializeKeyValueMap());
	}

	AutoCompleteResult result = new AutoCompleteResult();
	String searchString = name.trim().substring(0, name.length() - 1);
	String fixedFirstPart;
	if (searchString.contains(":")) {
	    fixedFirstPart = searchString.substring(0,
		    searchString.lastIndexOf(":") + 1);
	    Matcher m = Pattern.compile("(\\w*):[^:]*$").matcher(searchString);
	    m.find();
	    String lastKey = m.group(1);
	    String lastValue = searchString.substring(searchString
		    .lastIndexOf(":") + 1);
	    String valuePattern;
	    Set<String> includedValues = new LinkedHashSet<String>();
	    if (lastValue.contains(",")) {
		includedValues.addAll(Arrays.asList(lastValue.substring(0,
			lastValue.lastIndexOf(',')).split(",")));
		valuePattern = lastValue
			.substring(lastValue.lastIndexOf(",") + 1);
	    } else {
		valuePattern = lastValue;
	    }
	    for (String value : keyValueMap.get(lastKey)) {
		Set<String> proposedValues = new LinkedHashSet<String>(
			includedValues);
		if (value.startsWith(valuePattern.trim())) {
		    proposedValues.add(value);
		    result.add(fixedFirstPart + ' '
			    + Joiner.on(',').join(proposedValues));
		    result.setCount(result.getCount() + 1);
		}
	    }
	}
	// use the last word of the String to check for keywords
	fixedFirstPart = searchString.substring(0, searchString
		.lastIndexOf(' ') > 0 ? searchString.lastIndexOf(' ') : 0);
	String lastPart = searchString
		.substring(searchString.lastIndexOf(' ') + 1);
	for (String key : keyValueMap.keySet()) {
	    if (lastPart.length() > 0
		    && key.startsWith(lastPart.substring(0,
			    lastPart.length() - 1))) {
		result.add(fixedFirstPart + ' ' + key + ":");
		result.setCount(result.getCount() + 1);
	    }
	}
	return result;
    }

    @Override
    public void cancel() {
    }

}
