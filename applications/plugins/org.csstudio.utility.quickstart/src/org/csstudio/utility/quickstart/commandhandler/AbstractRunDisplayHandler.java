package org.csstudio.utility.quickstart.commandhandler;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.utility.quickstart.Activator;
import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;

/**
 * Abstract implementation of the class to open the sds file
 * selected in the quickstart menu. The class is abstract to avoid 
 * the explicit number of the sds file in the source code on this
 * level. (Each sds file needs a extra class with the 'run display'
 * command because of the extension point mechanism for the eclipse
 * menus.) 
 * 
 * @author jhatje
 *
 */
public abstract class AbstractRunDisplayHandler extends AbstractHandler {

	private static final String SDS_FILE_EXTENSION = "sds";
	private static final String PLT_FILE_EXTENSION = "plt";

	/**
	 * Get the sds file list from preferences.
	 */
	String[] getFileList() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		return prefs.getString(PreferenceConstants.SDS_FILES).split(";");
	}

	/**
	 * Opens the sds file for the given number in a display.
	 * 
	 * @param sdsFileList
	 * @param fileNo
	 */
	void openDisplay(String[] sdsFileList, int fileNo) {
		//Array starts with 0.
		int tmpfileNo = fileNo - 1;
		if (sdsFileList.length > tmpfileNo) {
			if ((sdsFileList[tmpfileNo] != null) && (sdsFileList[tmpfileNo].length() > 0) ) {
				//separate the filePath from menu name.
			    String[] filePathString = sdsFileList[tmpfileNo].split("\\?");
			    final IPath filePath = new Path(filePathString[0]);
			    IPath location = Platform.getLocation();
                IPath absolotPath = location.append(filePath);
				
				if (filePathString[0].endsWith(SDS_FILE_EXTENSION.toLowerCase())) {
					RunModeService.getInstance().openDisplayShellInRunMode(absolotPath);
            	}
            	if (filePathString[0].endsWith(PLT_FILE_EXTENSION.toLowerCase())) {
// TODO (jhatje): Enable when Databrwoser2 is integrated in CSS.
//            		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//						
//						@Override
//						public void run() {
//							IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(newPath); 
//							DB2Shell dbShell = new DB2Shell(file);
//							dbShell.openShell();
//						}
//					});
            	}
            }
        }
    }
}
