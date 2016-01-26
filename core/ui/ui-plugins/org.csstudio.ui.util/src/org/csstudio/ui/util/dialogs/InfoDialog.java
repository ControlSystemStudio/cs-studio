/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.dialogs;

import org.csstudio.ui.util.DialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Information message dialog subclass. Creates new shell, then a dialog with this shell as parent, because on Linux the
 * MessageDialog is opened in the background when running a fullscreen OPI.
 *
 * @author Boris Versic
 */
public class InfoDialog extends MessageDialog {

    public InfoDialog(Shell parent, String title, String message) {
        super(parent, title, null, message, MessageDialog.INFORMATION, new String[] { DialogConstants.OK_LABEL }, 0);
        /*
         * Note: Using SWT.ON_TOP for the dialog style forces the dialog to have the NO_TRIM style on Linux (no title
         * bar, no close button) - tested with gtk WM. Ref. on chosen solution (new shell):
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115#c18 and answer here:
         * https://dev.eclipse.org/mhonarc/lists/platform-swt-dev/msg07717.html
         */
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setBlockOnOpen(true);
    }

    public int open() {
        final Shell shell = new Shell(getParentShell().getDisplay(), SWT.NO_TRIM);
        setParentShell(shell);
        shell.setSize(100, 30);
        Rectangle windowBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
        // center horizontally, but place it a little higher vertically
        Point dialogCenter = new Point(windowBounds.x + windowBounds.width / 2,
                (windowBounds.y + windowBounds.height) / 2);
        Point location = new Point(dialogCenter.x - shell.getBounds().x / 2, dialogCenter.y - shell.getBounds().y / 2);
        shell.setLocation(location);
        int ans = super.open();
        shell.dispose();
        return ans;
    }

    public static void open(Shell parent, String title, String message) {
        new InfoDialog(parent, title, message).open();
    }
}
