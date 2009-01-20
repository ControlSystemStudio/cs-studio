package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.eclipse.jface.action.Action;

/** Action that restores a cell's value to the original value,
 *  replacing what the user might have entered.
 *  @author Kay Kasemir
 */
public class RestoreCellAction extends Action
{   
    final private Cell cells[];

    public RestoreCellAction(final Cell[] cells)
    {
        super(Messages.RestoreCell);
        setToolTipText(Messages.RestoreCell_TT);
        this.cells = cells;
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
        for (Cell cell : cells)
            if (cell.isEdited())
                cell.clearUserValue();
    }
}
