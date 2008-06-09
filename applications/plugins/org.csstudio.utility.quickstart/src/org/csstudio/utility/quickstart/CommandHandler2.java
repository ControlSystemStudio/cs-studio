package org.csstudio.utility.quickstart;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;

public class CommandHandler2 extends AbstractHandler {
 @Override
 public Object execute(ExecutionEvent event) throws ExecutionException {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String[] sdsFileList = prefs.getString(PreferenceConstants.SDS_FILE_1).split(";");
		if(sdsFileList.length > 1) {
			if((sdsFileList[1].length() > 0) && (sdsFileList[1] != null)) {
			    IPath newPath = new Path(sdsFileList[1]);
				RunModeService.getInstance().openDisplayShellInRunMode(newPath);
			}
		}		
 return null;
 }
}