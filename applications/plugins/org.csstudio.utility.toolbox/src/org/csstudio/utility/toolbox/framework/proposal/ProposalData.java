package org.csstudio.utility.toolbox.framework.proposal;

import org.eclipse.jface.fieldassist.IContentProposal;

public class ProposalData implements IContentProposal {

	private final String content;

	ProposalData(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public int getCursorPosition() {
		return content.length();
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
