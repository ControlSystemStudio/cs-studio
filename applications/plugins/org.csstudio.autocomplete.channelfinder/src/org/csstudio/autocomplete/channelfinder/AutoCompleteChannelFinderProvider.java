/**
 * 
 */
package org.csstudio.autocomplete.channelfinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;

/**
 * Autocomplete support using the Channelfinder directory service
 * 
 * @author shroffk
 * 
 */
public class AutoCompleteChannelFinderProvider implements IAutoCompleteProvider {

    private ChannelFinderClient client;

    @Override
    public boolean accept(ContentType type) {
	if (type.value().startsWith(ContentType.PV.value()))
	    return true;
	return false;
    }

    @Override
    public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
	AutoCompleteResult result = new AutoCompleteResult();
	if(desc.getValue().trim().length() > 8){
	    if (client == null) {
		client = ChannelFinder.getClient();
	    }
	    String trimmedName = AutoCompleteHelper.trimWildcards(desc.getValue().trim());
	    Pattern namePattern = AutoCompleteHelper.convertToPattern(trimmedName);
	    int count = 0;
	    for (Channel channel : client.findByName("*" + trimmedName + "*")) {
		if (count < limit) {
		    Proposal proposal = new Proposal(channel.getName(), false);
		    Matcher m = namePattern.matcher(channel.getName());
		    if (m.find()) {
			proposal.addStyle(ProposalStyle.getDefault(m.start(), m.end() - 1));
			result.addProposal(proposal);
			count++;
		    }
		} else {
		    result.setCount(count);
		    return result;
		}
	    }
	result.setCount(count);
	}
	return result;
    }

    @Override
    public void cancel() {

    }

}
