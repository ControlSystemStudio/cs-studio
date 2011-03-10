/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import java.io.FileInputStream;

import org.csstudio.display.pace.model.Model;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** Standalone GUI test, run as headless JUnit plugin test.
 *  For PV connections to work, use junit_customization.ini:
 *  
 *  When first using "Run/Run As/JUnit plugin test" on this file,
 *  it should start but the PVs will probably not connect
 *  because of missing ChannelAccess settings.
 *  
 *  Edit ChannelAccess settings in junit_customization.ini,
 *  then use "Run/Run Configurations..." to edit the run configuration
 *  which was initialized with defaults in the previous step by adding
 *  something like this under "Arguments", "Program Arguments":
 *  
 *   -pluginCustomization /full/path/to/the/Workspace/org.csstudio.display.pace/junit_customization.ini
 *
 *  While editing the Run Configuration, one can also select 
 *     Main, Program To Run, Application: No Application (headless mode)
 *  to prevent the rest of the Eclipse Workbench from starting up.
 *  
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/28/09
 */
@SuppressWarnings("nls")
public class GUITest
{
    @Test
    public void testGUI() throws Exception
    {
        // Open display & shell ourself.
        // Real application would have Eclipse Workbench site
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setBounds(10, 100, 800, 600);
        
        // Exceptions are not expected and handled by JUnit
        // framework by displaying them.
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        new GUI(shell, model, null);
        model.start();

        // SWT main loop, in real application handled by workbench
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        
        // Model shutdown
        model.stop();
    }
}
