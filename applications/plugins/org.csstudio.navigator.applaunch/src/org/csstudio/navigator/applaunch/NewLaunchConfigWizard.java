/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/** RCP Wizard for creating a new launch configuration
 *  @author Kay Kasemir
 */
public class NewLaunchConfigWizard extends Wizard implements INewWizard
{
	private IStructuredSelection selection;
	private LaunchConfigWizardFilePage file_page;
	private LaunchConfigWizardContentPage config_page;

	/** Track current selection */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.selection = selection;
	}

	/** {@inheritDoc} */
	@Override
    public void addPages()
    {
		file_page = new LaunchConfigWizardFilePage(selection);
		config_page = new LaunchConfigWizardContentPage();
		addPage(file_page);
		addPage(config_page);
    }

	/** {@inheritDoc} */
	@Override
	public boolean performFinish()
	{
		final LaunchConfig config = config_page.getConfig();
		file_page.setConfig(config);
		final IFile file = file_page.createNewFile();
		return file != null;
	}
}
