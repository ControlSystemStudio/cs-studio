package org.csstudio.opibuilder.wizards;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

/**A wizard for creating new OPI Files.
 * @author Xihui Chen
 *
 */
public class NewJavaScriptWizard extends Wizard implements INewWizard {
	
	private NewJavaScriptWizardPage jsFilePage;
	
	private IStructuredSelection selection;

	private IWorkbench workbench;

	
	@Override
	public void addPages() {
		jsFilePage =new NewJavaScriptWizardPage("JavaScriptFilePage", selection); //$NON-NLS-1$
		addPage(jsFilePage);
	}
	
	
	@Override
	public boolean performFinish() {
		IFile file = jsFilePage.createNewFile();

		if (file == null) {
			return false;
		}

		try {
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(file), "org.csstudio.opibuilder.jseditor");//$NON-NLS-1$
		} catch (PartInitException e) {
			MessageDialog.openError(null, "Open JavaScript File error", 
					"Failed to open the newly created JavaScript File. \n" + e.getMessage());
			CentralLogger.getInstance().error(this, e);
		}  
		
	   
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		
	}

}
