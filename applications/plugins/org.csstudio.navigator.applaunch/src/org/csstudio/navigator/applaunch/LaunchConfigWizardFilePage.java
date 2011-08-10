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
    private String command = "";
	private String icon_name = "icon:run";
	
	public LaunchConfigWizardFilePage(IStructuredSelection selection)
    {
		super(Messages.LaunchConfigTitle, selection);
		setTitle(Messages.LaunchConfigTitle);
		setDescription(Messages.FileWizardDescr);
    }

	public void setCommand(final String command)
    {
		this.command = command;
    }

	public void setIconName(final String icon_name)
    {
		this.icon_name = icon_name;
    }

	@Override
	protected InputStream getInitialContents()
	{
		final String xml =
			"<application>\n" +
			"  <command>" + command + "</command>\n" +
			"  <icon>" + icon_name + "</icon>\n" +
			"</application>\n";
		return new ByteArrayInputStream(xml.getBytes());
	}
	
	@Override
	public String getFileExtension()
	{
		return "app";
	}
}
