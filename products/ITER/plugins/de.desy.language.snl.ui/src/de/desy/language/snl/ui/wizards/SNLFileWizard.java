package de.desy.language.snl.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * This class implements the interface required by the desktop for creating C
 * Files.
 * 
 * @author mscarpino
 */
public class SNLFileWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;

	private IWorkbench workbench;

	private SNLFilePage mainPage;

	/**
	 * (non-Javadoc) Method declared on Wizard.
	 */
	@Override
	public void addPages() {
		this.mainPage = new SNLFilePage(this.workbench, this.selection);
		this.addPage(this.mainPage);
	}

	/**
	 * (non-Javadoc) Method declared on IWorkbenchWizard
	 */
	public void init(final IWorkbench workbench1,
			final IStructuredSelection selection1) {
		this.workbench = workbench1;
		this.selection = selection1;
		this.setWindowTitle("SNL File Creation Wizard");
//		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(
//				NewSNLProjectWizard.class, "sheet.gif"));
	}

	/**
	 * (non-Javadoc) Performs the finish() method of the <code>CFilePage</code>
	 */
	@Override
	public boolean performFinish() {
		return this.mainPage.finish();
	}
}
