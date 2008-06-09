package org.csstudio.utility.quickstart;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.utility.quickstart.preferences.PreferenceConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;


public class CommandHandler1 extends AbstractHandler {
 @Override
 public Object execute(ExecutionEvent event) throws ExecutionException {
//	    IWorkspaceRoot workspacePath = ResourcesPlugin.getWorkspace().getRoot();
//	    String path = workspacePath.getLocation().toString();
//	    path = path +"/test/test.css-sds";
//	    IPath ipath = new Path("C:\\CSS100ProductExport\\runtime-css.product(1)\\SDS Demo Display\\helpExample\\Action Display 1.css-sds");
////		RunModeService.getInstance().openDisplayShellInRunMode(ipath, null);
//	    String s = AddToQuickstartAction.sdsFile.getLocation().toString();
	    
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String[] sdsFileList = prefs.getString(PreferenceConstants.SDS_FILE_1).split(";");
		if(sdsFileList.length > 0) {
			if((sdsFileList[0].length() > 0) && (sdsFileList[0] != null)) {
			    IPath newPath = new Path(sdsFileList[0]);
				RunModeService.getInstance().openDisplayShellInRunMode(newPath);
			}
		}		
//		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile("/test/test.css-plt");
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		try {
//			EditorUtil.openEditor(page, file);
//		} catch (PartInitException e) {
//		}
		return null;
 }
}