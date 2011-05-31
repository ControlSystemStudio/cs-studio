/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.basic.epics;

import java.util.Map;

import org.csstudio.logging.LogConfigurator;
import org.csstudio.platform.workspace.RelaunchConstants;
import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.csstudio.startup.module.WorkbenchExtPoint;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/** Workbench handler
 *  @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a> - Underlying
 *  code example from org.csstudio.startuphelper.module.DefaultWorkbench
 *  @author Kay Kasemir
 */
public class BasicWorkbenchExt implements WorkbenchExtPoint
{
    @Override
    public Object beforeWorkbenchCreation(Display display,
            IApplicationContext context, Map<String, Object> parameters)
            throws Exception
    {
        //do nothing
        return null;
    }

    @Override
    public Object afterWorkbenchCreation(Display display,
            IApplicationContext context, Map<String, Object> parameters)
            throws Exception
    {
        //do nothing
        return null;
    }

    @Override
    public Object runWorkbench(Display display, IApplicationContext context,
            Map<String, Object> parameters) throws Exception
    {
        // Configure Logging
        try
        {
            LogConfigurator.configureFromPreferences();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // Continue without customized log configuration
        }

        int returnCode = PlatformUI.createAndRunWorkbench(display,
                new BasicWorkbenchAdvisor((OpenDocumentEventProcessor) parameters.get(
                        OpenDocumentEventProcessor.OPEN_DOC_PROCESSOR)));
        if (returnCode == PlatformUI.RETURN_RESTART) {
            // Something called IWorkbench.restart().
            // Is this supposed to be a RESTART or RELAUNCH?
            final Integer exitCode =
                Integer.getInteger(RelaunchConstants.PROP_EXIT_CODE);
            if (IApplication.EXIT_RELAUNCH.equals(exitCode)) {
                // RELAUCH with new command line
                return IApplication.EXIT_RELAUNCH;
            }
            // RESTART without changes
            return IApplication.EXIT_RESTART;
        }
        // Plain exit from IWorkbench.close()
        return IApplication.EXIT_OK;
    }
}
