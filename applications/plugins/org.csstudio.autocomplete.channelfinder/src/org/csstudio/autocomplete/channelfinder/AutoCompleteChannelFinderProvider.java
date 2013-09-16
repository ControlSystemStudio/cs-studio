/**
 * 
 */
package org.csstudio.autocomplete.channelfinder;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;

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
		if (type == ContentType.PVName)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		if (client == null) {
			client = ChannelFinder.getClient();
		}
		String trimmedName = desc.getValue().trim();
		for (Channel channel : client.findByName("*" + trimmedName + "*")) {
			Proposal proposal = new Proposal(channel.getName(), false);
			int from = channel.getName().indexOf(trimmedName);
			int to = from + trimmedName.length() - 1;
			proposal.addStyle(ProposalStyle.getDefault(from, to));
			result.addProposal(proposal);
		}
		return result;
	}

	@Override
	public void cancel() {

	}

}
