package org.csstudio.utility.quickstart.compoundcontribution;

import org.eclipse.jface.action.IContributionItem;

public class QuickstartCompoundContributionItem13 extends
		AbstractQuickstartCompoundContributionItem {

	/**
	 * ID for the command.
	 */
	private final String commandID = "org.csstudio.utility.quickstart.command13";

	/**
	 * ID for the compound.
	 */
	private final String compoundID = "org.csstudio.utility.quickstart.QuickstartCompoundContributionItem13";

	public QuickstartCompoundContributionItem13() {
	}

	public QuickstartCompoundContributionItem13(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemsForMenuNo(13, commandID, compoundID);
	}
}
