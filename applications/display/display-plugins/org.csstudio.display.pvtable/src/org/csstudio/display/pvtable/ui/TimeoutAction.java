/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;

/** {@link Action} to configure the completion timeout
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeoutAction extends PVTableAction
{
    public TimeoutAction(final TableViewer viewer)
    {
        super("Completion Timeout", "icons/timeout.png", viewer);
    }

    @Override
    public void run()
    {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null)
            return;

        final InputDialog dlg = new InputDialog(viewer.getControl().getShell(),
                "Completion Timeout",
                "Enter the timeout in seconds used for all items that are restored with 'completion' (put-callback)",
                Long.toString(model.getCompletionTimeout()), new IInputValidator()
                {
                    @Override
                    public String isValid(final String text)
                    {
                        try
                        {
                            if (Long.parseLong(text) > 0)
                                return null;
                        }
                        catch (NumberFormatException ex)
                        {
                            // Ignore, fall through
                        }
                        return Messages.EnterPositiveTolerance;
                    }
                });

        if (dlg.open() != Window.OK)
            return;

        model.setCompletionTimeout(Long.parseLong(dlg.getValue()));
        model.fireModelChange();
    }
}
