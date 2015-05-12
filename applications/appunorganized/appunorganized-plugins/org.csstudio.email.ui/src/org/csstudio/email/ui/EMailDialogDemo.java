/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the EMailDialog
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EMailDialogDemo
{
    final private static String host = "smtp.ornl.gov";
    final private static String from = "kasemirk@ornl.gov";
    final private static String to = from;

    @Test
    public void testEMailDialogWithImage()
    {
        final Shell shell = new Shell();


        final Dialog dlg = new EMailSenderDialog(shell, host, from, to, "Test",
                                                 "This is a test", "icons/mail-edit-48.png");
        dlg.open();
    }

    @Test
    public void testEMailDialogWithOutImage()
    {
        final Shell shell = new Shell();


        final Dialog dlg = new EMailSenderDialog(shell, host, from, to, "Test",
                                                 "This is a test", null);
        dlg.open();
    }
}
