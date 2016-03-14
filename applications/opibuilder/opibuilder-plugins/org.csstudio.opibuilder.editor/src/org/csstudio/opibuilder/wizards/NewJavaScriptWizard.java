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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

/**A wizard for creating new Javascript File.
 * @author Xihui Chen
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
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "JS Editor error", e); //$NON-NLS-1$
        }

        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }
}
