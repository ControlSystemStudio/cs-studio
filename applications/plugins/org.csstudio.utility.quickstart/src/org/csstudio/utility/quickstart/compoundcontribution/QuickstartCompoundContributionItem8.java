package org.csstudio.utility.quickstart.compoundcontribution;

import org.eclipse.jface.action.IContributionItem;

public class QuickstartCompoundContributionItem8 extends
		AbstractQuickstartCompoundContributionItem {

	/**
	 * ID for the command.
	 */
	private final String commandID = "org.csstudio.utility.quickstart.command8";

	/**
	 * ID for the compound.
	 */
	private final String compoundID = "org.csstudio.utility.quickstart.QuickstartCompoundContributionItem8";

	public QuickstartCompoundContributionItem8() {
	}

	public QuickstartCompoundContributionItem8(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemsForMenuNo(8, commandID, compoundID);
	}
}
