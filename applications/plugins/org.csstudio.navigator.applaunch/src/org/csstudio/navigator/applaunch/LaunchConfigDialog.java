/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/** Dialog for editing a {@link LaunchConfig}
 *  @author Kay Kasemir
 */
public class LaunchConfigDialog extends Dialog
{
	final private LaunchConfigUI gui;
	private LaunchConfig edited_config = null;
	
	/** Initialize
	 *  @param shell
	 *  @param config
	 */
	protected LaunchConfigDialog(final Shell shell, final LaunchConfig config)
    {
	    super(shell);
	    gui = new LaunchConfigUI(config);
    }

	@Override
    protected void configureShell(final Shell shell)
    {
	    super.configureShell(shell);
	    shell.setText(Messages.LaunchConfigTitle);
	    shell.setSize(400, 350);
    }

	/** Allow resize */
	@Override
    protected boolean isResizable()
    {
	    return true;
    }

	/** Display {@link LaunchConfigUI} inside dialog */
	@Override
    protected Control createDialogArea(final Composite parent)
    {
		return gui.createControl(parent);
    }
	
	@Override
    protected void okPressed()
    {
		edited_config = gui.getConfig();
	    super.okPressed();
    }

	/** @return {@link LaunchConfig} that the user entered or <code>null</code> on cancel */
	public LaunchConfig getConfig()
    {
	    return edited_config;
    }
}
