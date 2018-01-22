/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.time.format.DateTimeFormatter;

import org.csstudio.alarm.beast.msghist.gui.GUI;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit test of GUI as standalone SWT app.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUIDemo
{
    /** Initial window size */
    private static final int WIDTHS = 1000, HEIGHT = 800;


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

            final Model model = new Model(MessageRDBIT.URL, MessageRDBIT.USER, MessageRDBIT.PASSWORD, MessageRDBIT.SCHEMA, 1000, DateTimeFormatter.ISO_LOCAL_DATE_TIME, shell);
            new GUI(null, shell, model, null, null, false, false);

            shell.open();

            model.setTimerange("-1 days", "now");

            // Message loop left to the application
            while (!shell.isDisposed())
                if (!display.readAndDispatch())
                    display.sleep();
            display.dispose(); // !
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
