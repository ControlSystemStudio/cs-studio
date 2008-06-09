package org.csstudio.utility.quickstart;

import java.util.Collections;

import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

public class MyCompoundContributionItem1 extends CompoundContributionItem {
	private static int counter = 0;
	
	private String commandID = "org.csstudio.utility.quickstart.command1";
	
	private String menuText = "";
	
	public MyCompoundContributionItem1() {
	}

	public MyCompoundContributionItem1(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IContributionItem[] items = new IContributionItem[1];

		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String[] sdsFileList = prefs.getString(PreferenceConstants.SDS_FILE_1).split(";");
		if(sdsFileList.length > 0) {
			if((sdsFileList[0] != null) && (sdsFileList[0].length() > 1)) {
				String[] pathSegments = sdsFileList[0].split("/");
				String fileName = pathSegments[pathSegments.length -1];
				menuText = fileName;
				commandID = "org.csstudio.utility.quickstart.command1";
			}
		} else {
			commandID = "yyyy";
		}
		items[0] = new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				     "org.csstudio.utility.quickstart.myCompoundContributionItem1", commandID,
				     Collections.emptyMap(), null, null, null,
				     menuText, null, null, SWT.NONE);
		   return items;
	}

}
