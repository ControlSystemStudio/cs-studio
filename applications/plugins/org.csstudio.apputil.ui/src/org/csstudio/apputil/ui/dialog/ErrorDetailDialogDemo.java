/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.dialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of the {@link ErrorDetailDialog}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ErrorDetailDialogDemo
{
    public static void main(final String args[])
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);

        final ErrorDetailDialog dlg = new ErrorDetailDialog(shell,
                "Error",
                "Some problem.\nGo figure!",
                "The problem was caused by something that failed,\nand it is a good question what exactly happened.\nException code: 0x1234\ndsufhiufghiudshg ifduhgoisdfu hgiufdsh giudfh goisudfhgoiudsfhgoisudhfgoiudshfioughsdfoiuh");

        dlg.open();
        display.dispose(); // !
    }
}
