package org.csstudio.utility.quickstart;

import org.csstudio.platform.ui.util.EditorUtil;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class CommandHandler1 extends AbstractHandler {
 @Override
 public Object execute(ExecutionEvent event) throws ExecutionException {
	    IWorkspaceRoot workspacePath = ResourcesPlugin.getWorkspace().getRoot();
	    String path = workspacePath.getLocation().toString();
	    path = path +"/test/test.css-sds";
	    IPath ipath = new Path("C:\\CSS100ProductExport\\runtime-css.product(1)\\SDS Demo Display\\helpExample\\Action Display 1.css-sds");
//		RunModeService.getInstance().openDisplayShellInRunMode(ipath, null);
	    String s = AddToQuickstartAction.sdsFile.getLocation().toString();
	    IPath newPath = new Path(s);
		RunModeService.getInstance().openDisplayShellInRunMode(newPath);
		
//		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile("/test/test.css-plt");
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		try {
//			EditorUtil.openEditor(page, file);
//		} catch (PartInitException e) {
//		}
   return null;
 }
}