package org.csstudio.utility.quickstart.compoundcontribution;

import org.eclipse.jface.action.IContributionItem;

public class QuickstartCompoundContributionItem9 extends
		AbstractQuickstartCompoundContributionItem {

	/**
	 * ID for the command.
	 */
	private final String commandID = "org.csstudio.utility.quickstart.command9";

	/**
	 * ID for the compound.
	 */
	private final String compoundID = "org.csstudio.utility.quickstart.QuickstartCompoundContributionItem9";

	public QuickstartCompoundContributionItem9() {
	}

	public QuickstartCompoundContributionItem9(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemsForMenuNo(9, commandID, compoundID);
	}
}
