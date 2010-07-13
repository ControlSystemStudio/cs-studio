/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit test of GUI as standalone SWT app.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUITest
{
    /** Initial window size */
    private static final int WIDTHS = 1000, HEIGHT = 800;

    /** URL for RDB that holds log messages */
//    private static final String URL =
//            "jdbc:mysql://titan-terrier.sns.ornl.gov/log?user=log&password=$log";
//    private static final String USER = "log";
//    private static final String PASSWORD = "$log";
    /** Database schema (Set to "" if not used) */
//    private static final String SCHEMA = "";

    private static final String URL =
        "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
    private static final String USER = "sns_reports";
    private static final String PASSWORD = "sns";
    private static final String SCHEMA = "EPICS";

    @Test
    public void testGUI()
    {
        try
        {
            final Display display = new Display();
            final Shell shell = new Shell(display);
            final Rectangle screen = display.getBounds();
            shell.setBounds((screen.width-WIDTHS)/2,
                    (screen.height-HEIGHT)/2, WIDTHS, HEIGHT);
            
            final Model model = new Model(URL, USER, PASSWORD, SCHEMA, 1000);
            new GUI(null, shell, model);
    
            shell.open();
    
            model.setTimerange("-1 days", "now");
            
            // Message loop left to the application
            while (!shell.isDisposed())
                if (!display.readAndDispatch())
                    display.sleep();
            display.dispose(); // !
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
