package org.csstudio.opibuilder.editor;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The Action to Run an OPI.
 * @author Xihui Chen
 *
 */
public class RunOPIAction extends Action{

  public RunOPIAction() {
	 super("Run OPI", CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			 OPIBuilderPlugin.PLUGIN_ID, "icons/run.gif"));
  }

  @Override
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		if(activeEditor instanceof OPIEditor){
			DisplayModel displayModel = ((OPIEditor)activeEditor).getDisplayModel();
			IEditorInput input = activeEditor.getEditorInput();
			IFile file = null;
			if(input instanceof FileEditorInput)
				file = ((FileEditorInput)input).getFile();
			RunModeService.getInstance().runOPI(file, displayModel);
		}
			
	}
	
}
