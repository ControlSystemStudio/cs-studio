package org.csstudio.utility.quickstart;

import java.util.Collections;

import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

public class MyCompoundContributionItem2 extends CompoundContributionItem {
	private static int counter = 0;
	
	private String commandID = "org.csstudio.utility.quickstart.command2";
	
	private String menuText = "";
	
	public MyCompoundContributionItem2() {
	}

	public MyCompoundContributionItem2(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IContributionItem[] items = new IContributionItem[1];

		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String[] sdsFileList = prefs.getString(PreferenceConstants.SDS_FILE_1).split(";");
		if(sdsFileList.length > 1) {
			if((sdsFileList[1] != null) && (sdsFileList[1].length() > 1)) {
				String[] pathSegments = sdsFileList[1].split("/");
				String fileName = pathSegments[pathSegments.length -1];
				menuText = fileName;
				commandID = "org.csstudio.utility.quickstart.command2";
			}
		} else {
			commandID = "xxx";
		}
		items[0] = new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				     "org.csstudio.utility.quickstart.myCompoundContributionItem2", commandID,
				     Collections.emptyMap(), null, null, null,
				     menuText, null, null, SWT.NONE);
		   return items;
	}

}