package org.csstudio.utility.quickstart.compoundcontribution;

import java.util.Collections;

import org.csstudio.utility.quickstart.Activator;
import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

public abstract class AbstractQuickstartCompoundContributionItem extends
		CompoundContributionItem {


	/**
	 * temporary ID, because we have to change the ID if the menu item should be
	 * invisible.
	 */
	private String currentCommandID;

	/**
	 * Text for the menu item in the quickstart menu.
	 */
	private String menuText = "";

	public AbstractQuickstartCompoundContributionItem() {
	}

	public AbstractQuickstartCompoundContributionItem(String id) {
		super(id);
	}

	IContributionItem[] getItemsForMenuNo(int menuNo, String commandIDNo, String compoundIDNo) {
		//Array starts with 0.
		menuNo = menuNo - 1;
		IContributionItem[] items = new IContributionItem[1];
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String[] sdsFileList = prefs.getString(PreferenceConstants.SDS_FILES)
				.split(";");
		if (sdsFileList.length > menuNo) {
			if ((sdsFileList[menuNo] != null) && (sdsFileList[menuNo].length() > 1)) {
				//separate the filePath from menu name.
				String[] filePath = sdsFileList[menuNo].split("\\?");
				String[] pathSegments = filePath[0].split("/");
				String fileName = pathSegments[pathSegments.length - 1];
				if((filePath.length < 2) || (filePath[1]==null) || (filePath[1].equals(""))) {
					menuText = fileName;
				} else {
					menuText = filePath[1];
				}
				currentCommandID = commandIDNo;
			}
		} else {
			currentCommandID = "xx";
		}
		items[0] = new CommandContributionItem(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow(),
				compoundIDNo,
				currentCommandID, Collections.emptyMap(), null, null, null, menuText,
				null, null, SWT.NONE);
		return items;
		
	}
}
