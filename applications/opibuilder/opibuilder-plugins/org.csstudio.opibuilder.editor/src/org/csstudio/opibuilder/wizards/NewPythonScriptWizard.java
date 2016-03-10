/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.wizards;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

/**A wizard for creating new Python script file
 * @author Xihui Chen
 */
public class NewPythonScriptWizard extends Wizard implements INewWizard {

    private NewPythonScriptWizardPage pyFilePage;

    private IStructuredSelection selection;

    private IWorkbench workbench;

    @Override
    public void addPages() {
        pyFilePage =new NewPythonScriptWizardPage("PythonScriptFilePage", selection); //$NON-NLS-1$
        addPage(pyFilePage);
    }

    @Override
    public boolean performFinish() {
        IFile file = pyFilePage.createNewFile();

        if (file == null) {
            return false;
        }
          // Open editor on new file.
        IWorkbenchWindow dw = workbench.getActiveWorkbenchWindow();
        try {
            if (dw != null) {
                IWorkbenchPage page = dw.getActivePage();
                if (page != null) {
                    IDE.openEditor(page, file, true);
                }
            }
        } catch (PartInitException e) {
            MessageDialog.openError(null, "Open Python Script File error",
                    "Failed to open the newly created Python Script File. \n" + e.getMessage());
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Python Editor error", e); //$NON-NLS-1$
        }

        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }
}
