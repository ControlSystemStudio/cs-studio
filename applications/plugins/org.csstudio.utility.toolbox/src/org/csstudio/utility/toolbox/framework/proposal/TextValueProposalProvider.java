package org.csstudio.utility.toolbox.framework.proposal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class TextValueProposalProvider implements IContentProposalProvider {

	private List<? extends TextValue> data;

	public void setData(List<? extends TextValue> data) {
		this.data = data;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		return guessTextValueFromContents(contents);
	}

	private IContentProposal[] guessTextValueFromContents(String contents) {
		
		List<IContentProposal> proposalList = new ArrayList<IContentProposal>();
		
		if (StringUtils.isEmpty(contents)) {
			for (TextValue textValue: data) {
				proposalList.add(new ProposalData(textValue.getValue()));
			}
		} else {
			for (TextValue textValue: data) {
				String value = textValue.getValue();
				if (value.contains(contents)) {
					proposalList.add(new ProposalData(textValue.getValue()));					
				}
			}			
		}
				
		return proposalList.toArray(new IContentProposal[proposalList.size()]);
						
	}

	public TextValueProposalProvider(List<? extends TextValue> data) {
		this.data = data;
	}

}
