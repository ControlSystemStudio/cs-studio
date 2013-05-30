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

/** Action that restores a cell's value to the original value,
 *  replacing the "user" value.
 *  
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
public class RestoreCellAction extends Action
{   
    final private Cell cells[];

    public RestoreCellAction(final Cell[] cells)
    {
        super(Messages.RestoreCell);
        // On most OS, the action tool tip doesn't show anywhere,
        // but we set it anyway
        setToolTipText(Messages.RestoreCell_TT);
        this.cells = cells;
        // Only enable the action for cells that were actually
        // edited. Otherwise, there would be nothing to restore.
        boolean enabled = false;
        if (cells != null)
            for (Cell cell : cells)
                if (cell.isEdited())
                {
                    enabled = true;
                    break;
                }
        setEnabled(enabled);
    }

    @Override
    public void run()
    {
        // Explain "clearUserValue"?
        // It's explained in the javadoc of clearUserValue().
        for (Cell cell : cells)
            if (cell.isEdited())
                cell.clearUserValue();
    }
}
