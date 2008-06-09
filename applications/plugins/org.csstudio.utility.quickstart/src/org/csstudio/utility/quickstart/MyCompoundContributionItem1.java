package org.csstudio.utility.quickstart;

import java.util.Collections;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

public class MyCompoundContributionItem1 extends CompoundContributionItem {
	private static int counter = 0;
	public MyCompoundContributionItem1() {
		// TODO Auto-generated constructor stub
	}

	public MyCompoundContributionItem1(String id) {
		super(id);
//		System.out.println("XXXXXXXXXXXXXXX " +id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IContributionItem[] items = new IContributionItem[1];
		items[0] = new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				     "org.csstudio.utility.quickstart.myCompoundContributionItem1", "org.csstudio.utility.quickstart.command1",
				     Collections.emptyMap(), null, null, null,
				     "Dynamic Menu "+ counter++, null, null, SWT.NONE);
		   return items;
	}

}
