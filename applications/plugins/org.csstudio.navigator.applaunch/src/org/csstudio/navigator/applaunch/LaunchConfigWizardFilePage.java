/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/** Wizard page to select file name for new launch config
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LaunchConfigWizardFilePage extends WizardNewFileCreationPage
{
    private LaunchConfig config = new LaunchConfig();
	
	public LaunchConfigWizardFilePage(IStructuredSelection selection)
    {
		super(Messages.LaunchConfigTitle, selection);
		setTitle(Messages.LaunchConfigTitle);
		setDescription(Messages.FileWizardDescr);
    }

	public void setConfig(final LaunchConfig config)
    {
		this.config = config;
    }

	@Override
    public boolean isPageComplete()
    {
		// Need at least ".app"
	    return super.isPageComplete()  ||  getFileName().length() > 4;
    }

	@Override
	protected InputStream getInitialContents()
	{
		return new ByteArrayInputStream(config.getXML().getBytes());
	}
	
	@Override
	public String getFileExtension()
	{
		return "app";
	}
}
