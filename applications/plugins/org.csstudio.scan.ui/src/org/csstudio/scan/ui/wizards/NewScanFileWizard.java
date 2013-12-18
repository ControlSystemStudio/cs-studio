/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.scan.ui.wizards;

import java.util.logging.Level;

import org.csstudio.scan.ui.ScanUIActivator;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The Class NewScanFileWizard.
 * @author benhad naceur @ sopra group 
 */
public class NewScanFileWizard extends Wizard implements INewWizard {

	/** The scan file page. */
	private NewScanFileWizardPage scanFilePage;

	/** The selection. */
	private IStructuredSelection selection;

	/** The workbench. */
	private IWorkbench workbench;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		scanFilePage =new NewScanFileWizardPage("ScanFilePage", selection); 
		addPage(scanFilePage);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		IFile file = scanFilePage.createNewFile();

		if (file == null) {
			return false;
		}

		try {
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(file), "org.csstudio.scan.ui.scantree.editor");
		} catch (PartInitException e) {
			MessageDialog.openError(null, "Open Scan File error",
					"Failed to open the newly created Scan File. \n" + e.getMessage());
            ScanUIActivator.getLogger().log(Level.WARNING, "Scan activation error", e);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

}
