package org.csstudio.opibuilder.actions;

import java.io.FileNotFoundException;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The Action to Run an OPI.
 * @author Xihui Chen
 *
 */
public class RunOPIAction extends Action{

	public static String ID = "org.csstudio.opibuilder.editor.run"; //$NON-NLS-1$
	public static String ACITON_DEFINITION_ID = "org.csstudio.opibuilder.runopi"; //$NON-NLS-1$
	
  public RunOPIAction() {
	 super("Run OPI", CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			 OPIBuilderPlugin.PLUGIN_ID, "icons/run.gif"));	 //$NON-NLS-1$
	 setId(ID);
	 setActionDefinitionId(ACITON_DEFINITION_ID);
  }

  @Override
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if(activeEditor instanceof OPIEditor){
			DisplayModel displayModel = ((OPIEditor)activeEditor).getDisplayModel();
			IEditorInput input = activeEditor.getEditorInput();
			//TODO: add run option: sync with editor.
			IFile file = null;
			if(input instanceof FileEditorInput)
				file = ((FileEditorInput)input).getFile();
			else if (input instanceof FileStoreEditorInput) {
				IPath path = URIUtil.toPath(((FileStoreEditorInput) input)
						.getURI());
				//read file
				IFile[] files = 
					ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
							ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path));
				
				if(files.length < 1)
					try {
						throw new FileNotFoundException("The file " + path.toString() + "does not exist!");
					} catch (FileNotFoundException e) {
						CentralLogger.getInstance().error(this, e);
						MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
								e.getMessage());					
					}
				
				file = files[0];
			}
			RunModeService.getInstance().runOPI(file, displayModel, TargetWindow.RUN_WINDOW, null);
		}
			
	}
}
