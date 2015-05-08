package org.csstudio.sds.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewScriptWizard extends Wizard implements INewWizard {

    /**
     * This wizard page is used to enter the file name and the target
     * project/folder for the new script rule.
     */
    private NewScriptWizardPage _sdsScriptPage;

    /**
     * The current selection.
     */
    private IStructuredSelection _selection;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        _sdsScriptPage = new NewScriptWizardPage("sdsScript", //$NON-NLS-1$
                _selection);
        addPage(_sdsScriptPage);
    }

    @Override
    public boolean performFinish() {
        boolean result = true;
        IFile file = _sdsScriptPage.createNewFile();

        if (file == null) {
            result = false;
        }

        return result;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        _selection = selection;
    }

}
