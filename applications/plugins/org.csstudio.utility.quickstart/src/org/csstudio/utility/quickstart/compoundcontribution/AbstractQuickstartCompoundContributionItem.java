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


/**
 * Abstract class that creates the command for the quickstart menu.
 * Here are all stuff that is independent from a hard coded 
 * menu number to avoid duplicated code.
 * 
 * @author jhatje
 *
 */
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

	/**
	 * Method called from the concrete menu implementation.
	 * If a item is set for the 'menuNo' a valid command
	 * with the menu name or if not set the file name is set.
	 * Otherwise an invalid command that will not be displayed
	 * will be set.
	 * 
	 * @param menuNo Number of the menu command
	 * @param commandIDNo 
	 * @param compoundIDNo
	 * @return
	 */
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
				//get the file name from the complete path.
				String[] pathSegments = filePath[0].split("/");
				String fileName = pathSegments[pathSegments.length - 1];
				if((filePath.length < 2) || (filePath[1]==null) || (filePath[1].equals(""))) {
					//A special menu name was not set by the user
					//-> using the file name
					menuText = fileName;
				} else {
					//Set the menu name 
					menuText = filePath[1];
				}
				currentCommandID = commandIDNo;
			} else {
				//Set an invalid command id that the menu item will not be
				//displayed in the menu.
				currentCommandID = "xx";
			}
		} else {
			//Set an invalid command id that the menu item will not be
			//displayed in the menu.
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
