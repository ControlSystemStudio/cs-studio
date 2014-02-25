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
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;

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
	public boolean accept(ContentType type) {
		return true;
	}

	@Override
	public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
		if (keyValueMap == null) {
			keyValueMap = Collections.unmodifiableMap(initializeKeyValueMap());
		}

		AutoCompleteResult result = new AutoCompleteResult();
		String searchString = desc.getOriginalContent().trim();
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
					String entry = fixedFirstPart + ' '
							+ Joiner.on(',').join(proposedValues);
					Proposal proposal = new Proposal(entry, false);
					proposal.addStyle(ProposalStyle.getDefault(
							fixedFirstPart.length(), fixedFirstPart.length()
									+ (valuePattern.length() - 1)));
					result.addProposal(proposal);
					result.setCount(result.getCount() + 1);
				}
			}
		}
		// use the last word of the String to check for keywords
		fixedFirstPart = searchString.substring(0, searchString
				.lastIndexOf(' ') > 0 ? searchString.lastIndexOf(' ') + 1 : 0);
		String lastValue = searchString
				.substring(searchString.lastIndexOf(":") + 1);
		if (!fixedFirstPart.isEmpty()) {
			String lastPart = searchString.substring(searchString
					.lastIndexOf(' ') + 1);
			if (!lastValue.isEmpty()
					&& !lastValue.trim().equals(lastPart.trim())) {
				for (String key : keyValueMap.keySet()) {
					if (lastPart.length() > 0
							&& key.startsWith(lastPart.substring(0,
									lastPart.length()))) {
						String entry = fixedFirstPart + key + ":";
						Proposal proposal = new Proposal(entry, true);
						proposal.addStyle(ProposalStyle.getDefault(
								fixedFirstPart.length(), fixedFirstPart.length()
										+ (lastPart.length() - 1)));
						result.addProposal(proposal);
						result.setCount(result.getCount() + 1);
					}
				}
			}
		}
		if (!lastValue.isEmpty()) {
			for (String key : keyValueMap.keySet()) {
				result.addProposal(new Proposal(searchString + ' ' + key + ":",
						true));
				result.setCount(result.getCount() + 1);
			}
		}
		// handle tooltip
		TooltipData td = new TooltipData();
		td.value = "<text> [<keyword>: <value>[, <value>]]";
		if (!Pattern.compile("^[^:]+(\\s\\w+:\\s*\\w+\\s*(,\\s*\\w+\\s*)*)*$")
				.matcher(searchString).matches()) {
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getError(0, td.value.length() - 1);
		}
		result.addTooltipData(td);
		return result;
	}

	@Override
	public void cancel() {
	}

}
