/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that sets several cells to the same user-supplied value
 *  @author Kay Kasemir
 *
 *      reviewed by Delphy 01/29/09
 */
public class SetCellValueAction extends Action
{
    final private Shell shell;
    final private Cell cells[];

    /** Initialize
     *  @param shell Shell for popup dialog
     *  @param cells Currently selected Model Cell entries
     */
    public SetCellValueAction(final Shell shell, final Cell[] cells)
    {
        super(Messages.SetValue);
        setToolTipText(Messages.SetValue_TT);
        this.shell = shell;
        this.cells = cells;
        // Only enable if there are cells to set.
        if (cells == null  ||  cells.length <= 0)
            setEnabled(false);
    }

    @Override
    public void run()
    {
        // Using value of first selected cell as suggestion,
        // prompt for value to be put into all selected cells
        boolean writable = true;
        for (Cell cell : cells)
            if (!cell.isPVWriteAllowed())
            {
                writable = false;
                break;
            }
        final String message =
            NLS.bind(writable
                     ? Messages.SetValue_Msg
                     : Messages.SetValue_Msg_WithReadonlyWarning,
                     cells[0].getColumn().getName());
        final InputDialog input = new InputDialog(shell, Messages.SetValue_Title,
                message, cells[0].getValue(), null);
        if (input.open() != Window.OK)
            return;
        // Update value of selected cells
        final String user_value = input.getValue();
        for (Cell cell : cells)
            cell.setUserValue(user_value);
    }
}
