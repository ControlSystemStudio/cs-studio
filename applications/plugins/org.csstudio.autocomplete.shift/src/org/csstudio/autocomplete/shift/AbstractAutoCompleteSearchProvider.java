/**
 * 
 */
package org.csstudio.autocomplete.shift;

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
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;

import com.google.common.base.Joiner;


public abstract class AbstractAutoCompleteSearchProvider implements IAutoCompleteProvider {

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
public boolean accept(ContentType type) {
	return true;
}

@Override
public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
	if (keyValueMap == null) {
		keyValueMap = Collections.unmodifiableMap(initializeKeyValueMap());
	}
	
	final AutoCompleteResult result = new AutoCompleteResult();
	final String searchString = desc.getOriginalContent().trim();
	String fixedFirstPart;
	if (searchString.contains(":")) {
		fixedFirstPart = searchString.substring(0, searchString.lastIndexOf(":") + 1);
		final Matcher m = Pattern.compile("(\\w*):[^:]*$").matcher(searchString);
		m.find();
		final String lastKey = m.group(1);
		final String lastValue = searchString.substring(searchString.lastIndexOf(":") + 1);
		String valuePattern;
		final Set<String> includedValues = new LinkedHashSet<String>();
		if (lastValue.contains(",")) {
			includedValues.addAll(Arrays.asList(lastValue.substring(0, lastValue.lastIndexOf(',')).split(",")));
			valuePattern = lastValue.substring(lastValue.lastIndexOf(",") + 1);
		} else {
			valuePattern = lastValue;
		}
		if(keyValueMap.containsKey(lastKey)) {
			for (String value : keyValueMap.get(lastKey)) {
				Set<String> proposedValues = new LinkedHashSet<String>(includedValues);
				if (value.startsWith(valuePattern.trim())) {
					proposedValues.add(value);
					final String entry = fixedFirstPart + ' '+ Joiner.on(',').join(proposedValues);
					final Proposal proposal = new Proposal(entry, false);
					proposal.addStyle(ProposalStyle.getDefault(fixedFirstPart.length(), fixedFirstPart.length()	+ (valuePattern.length() - 1)));
					result.addProposal(proposal);
					result.setCount(result.getCount() + 1);
				}
			}
		}
	}
	// use the last word of the String to check for keywords
	fixedFirstPart = searchString.substring(0, searchString.lastIndexOf(' ') > 0 ? searchString.lastIndexOf(' ') + 1 : 0);
	if (!fixedFirstPart.isEmpty()) {
		final String lastPart = searchString.substring(searchString.lastIndexOf(' ') + 1);
		for (String key : keyValueMap.keySet()) {
			if (lastPart.length() > 0 && key.startsWith(lastPart.substring(0,lastPart.length()))) {
				final String entry = fixedFirstPart + key + ":";
				final Proposal proposal = new Proposal(entry, true);
				proposal.addStyle(ProposalStyle.getDefault(fixedFirstPart.length(), fixedFirstPart.length() + (lastPart.length() - 1)));
				result.addProposal(proposal);
				result.setCount(result.getCount() + 1);
			}
		}
	}
	for (String key : keyValueMap.keySet()) {
		result.addProposal(new Proposal(searchString + ' ' + key + ":", false));
		result.setCount(result.getCount() + 1);
	
	}
	return result;
}

@Override
public void cancel() {
}

}