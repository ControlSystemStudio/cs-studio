package org.csstudio.opibuilder.actions;

import java.io.FileNotFoundException;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

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
			if(PreferencesHelper.isAutoSaveBeforeRunning() && activeEditor.isDirty())
				activeEditor.doSave(null);
			
			//It seems that the synch with editor is not necessary
			DisplayModel displayModel = ((OPIEditor)activeEditor).getDisplayModel();
			
			IEditorInput input = activeEditor.getEditorInput();	
			
			
			IFile file = null;
			try {
				file = ResourceUtil.getFileInEditor(input);
				RunModeService.getInstance().runOPI(file, TargetWindow.RUN_WINDOW, displayModel.getSize());
			} catch (FileNotFoundException e) {
				String message = "Failed to open OPI file: " + file;  
				CentralLogger.getInstance().error(this, message, e);				
				MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
						e.getMessage());
				ConsoleService.getInstance().writeError(message + "\n" + e);
			}			
		}
			
	}
}
