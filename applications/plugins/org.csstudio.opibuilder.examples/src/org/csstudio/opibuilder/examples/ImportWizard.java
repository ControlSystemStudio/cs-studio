/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.examples;

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

/**The import wizard to import BOY Examples.
 * @author Xihui Chen
 *
 */
public class ImportWizard extends Wizard implements IImportWizard {
	@Override
	public boolean performFinish() {
		new InstallExamplesAction().run(null);
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	    // NOP
	}


	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Import BOY Examples");
		addPage(new WizardPage("BOY Examples") {

			public void createControl(Composite parent) {
				setTitle("Import BOY Examples");
				setDescription("Import the OPI Examples come with BOY");
				Composite container = new Composite(parent, SWT.None);
				container.setLayout(new GridLayout());
				setControl(container);


				Label label = new Label(container, SWT.WRAP);
				GridData gd = new GridData();
				gd.widthHint = 500;
				label.setLayoutData(gd);

				label.setText("BOY Examples will be imported to your workspace. " +
						NLS.bind("If there is already a project named \"{0}\" in your workspace," +
								"the import will fail. ",
								InstallExamplesAction.PROJECT_NAME) +
								"Please rename or delete it and import again.");
			}
		});
	}
}
