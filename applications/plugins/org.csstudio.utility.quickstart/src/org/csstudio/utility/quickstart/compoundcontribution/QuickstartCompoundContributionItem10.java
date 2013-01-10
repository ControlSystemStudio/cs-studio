package org.csstudio.utility.quickstart.compoundcontribution;

import org.eclipse.jface.action.IContributionItem;

public class QuickstartCompoundContributionItem10 extends
		AbstractQuickstartCompoundContributionItem {

	/**
	 * ID for the command.
	 */
	private final String commandID = "org.csstudio.utility.quickstart.command10";

	/**
	 * ID for the compound.
	 */
	private final String compoundID = "org.csstudio.utility.quickstart.QuickstartCompoundContributionItem10";

	public QuickstartCompoundContributionItem10() {
	}

	public QuickstartCompoundContributionItem10(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemsForMenuNo(10, commandID, compoundID);
	}
}
