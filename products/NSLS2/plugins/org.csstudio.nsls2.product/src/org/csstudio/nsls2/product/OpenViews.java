/**
 * 
 */
package org.csstudio.nsls2.product;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.actions.ContributionItemFactory;

/**
 * @author shroffk
 * 
 */
public class OpenViews extends CompoundContributionItem {

	@Override
	protected IContributionItem[] getContributionItems() {
		List<IContributionItem> menuContributionList = new ArrayList<IContributionItem>();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IContributionItem item = ContributionItemFactory.VIEWS_SHORTLIST
				.create(window);
		menuContributionList.add(item); // add the list of views in the menu
		return menuContributionList
				.toArray(new IContributionItem[menuContributionList.size()]);

	}

}
