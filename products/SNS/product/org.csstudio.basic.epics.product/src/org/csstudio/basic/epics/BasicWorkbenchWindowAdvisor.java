/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.basic.epics;

import org.csstudio.logging.ui.ConsoleViewHandler;
import org.csstudio.ui.menu.app.ApplicationActionBarAdvisor;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Configure the workbench window.
 *  @author Kay Kasemir
 */
public class BasicWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
    public BasicWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }

    /** Set initial workbench window size and title */
    @Override
    public void preWindowOpen()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowFastViewBars(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowStatusLine(true);
    }

    @Override
    public void postWindowCreate()
    {
        super.postWindowCreate();

        // Add console view to the logger
        ConsoleViewHandler.addToLogger();
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }
}
