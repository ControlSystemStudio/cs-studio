/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/** Wizard page to configure the new launch config
 *  @author Kay Kasemir
 */
public class LaunchConfigWizardContentPage extends WizardPage
{
	final private LaunchConfigUI gui = new LaunchConfigUI(new LaunchConfig());
	
	public LaunchConfigWizardContentPage()
    {
	    super(Messages.LaunchConfigTitle);
	    setTitle(Messages.LaunchConfigTitle);
	    setDescription(Messages.ConfigureDescr);
    }

	@Override
    public void createControl(final Composite parent)
    {
		final Composite box = gui.createControl(parent);
			
		// Have to do this, see API of createControl()
		setControl(box);
    }
	
	/** @return LaunchConfig that the user selected */
    public LaunchConfig getConfig()
    {
    	return gui.getConfig();
    }
}
