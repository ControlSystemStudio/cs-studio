/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.imagelib;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * The import wizard of BOY Symbol Images Library.
 */
public class ImportWizard extends Wizard implements IImportWizard {

	private static final String WINDOW_TITLE = "Import BOY Image Library";
	private static final String WIZARD_PAGE = "BOY Image Library";
	private static final String WIZARD_PAGE_TITLE = "Import BOY Image Library";
	private static final String WIZARD_PAGE_DESCRIPTION = "Import the library of images come with BOY. "
			+ "It containes more than 200 electrical and fluid symbols.";

	@Override
	public boolean performFinish() {
		new InstallLibraryAction().run(null);
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// NOP
	}

	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle(WINDOW_TITLE);
		addPage(new WizardPage(WIZARD_PAGE) {

			public void createControl(Composite parent) {
				setTitle(WIZARD_PAGE_TITLE);
				setDescription(WIZARD_PAGE_DESCRIPTION);
				Composite container = new Composite(parent, SWT.None);
				container.setLayout(new GridLayout());
				setControl(container);

				Label label = new Label(container, SWT.WRAP);
				GridData gd = new GridData();
				gd.widthHint = 500;
				label.setLayoutData(gd);

				label.setText("BOY Symbol Images Library will be imported to your workspace. "
						+ NLS.bind(
								"If there is already a project named \"{0}\" in your workspace,"
										+ "the import will fail. ",
								InstallLibraryAction.PROJECT_NAME)
						+ "Please rename or delete it and import again.");
			}
		});
	}
}
