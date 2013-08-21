/**
 * 
 */
package org.csstudio.autocomplete.channelfinder;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinder;
import gov.bnl.channelfinder.api.ChannelFinderClient;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.DefaultProposalStyle;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.Proposal;

/**
 * Autocomplete support using the Channelfinder directory service
 * 
 * @author shroffk
 *
 */
public class AutoCompleteChannelFinderProvider implements IAutoCompleteProvider {
    
    private ChannelFinderClient client;

    @Override
    public AutoCompleteResult listResult(String type, String name, int limit) {
	AutoCompleteResult result = new AutoCompleteResult();	
	if(client == null){
	    client = ChannelFinder.getClient();	    
	}
	String trimmedName = name.substring(0, name.length()-1);
	for (Channel channel : client.findByName("*"+ trimmedName + "*")) {
	    Proposal proposal = new Proposal(channel.getName(), false);
	    int from = channel.getName().indexOf(trimmedName);
	    int to = from + trimmedName.length() - 1;
	    proposal.addStyle(new DefaultProposalStyle(from, to));
	    result.addProposal(proposal);
	}	
	return result;
    }

    @Override
    public void cancel() {
	
    }

}
