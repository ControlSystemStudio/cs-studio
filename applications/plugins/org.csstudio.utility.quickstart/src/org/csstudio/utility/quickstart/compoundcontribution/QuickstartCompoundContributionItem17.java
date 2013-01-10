package org.csstudio.utility.quickstart.compoundcontribution;

import org.eclipse.jface.action.IContributionItem;

public class QuickstartCompoundContributionItem17 extends
		AbstractQuickstartCompoundContributionItem {

	/**
	 * ID for the command.
	 */
	private final String commandID = "org.csstudio.utility.quickstart.command17";

	/**
	 * ID for the compound.
	 */
	private final String compoundID = "org.csstudio.utility.quickstart.QuickstartCompoundContributionItem17";

	public QuickstartCompoundContributionItem17() {
	}

	public QuickstartCompoundContributionItem17(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemsForMenuNo(17, commandID, compoundID);
	}
}
