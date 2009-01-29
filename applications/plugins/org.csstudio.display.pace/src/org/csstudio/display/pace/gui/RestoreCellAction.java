package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.eclipse.jface.action.Action;

/** Action that restores a cell's value to the original value,
 *  replacing what the user might have entered.
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
//TODO Explain "replacing what the user ..." refers to the table cell
public class RestoreCellAction extends Action
{   
    final private Cell cells[];

    public RestoreCellAction(final Cell[] cells)
    {
        super(Messages.RestoreCell);
        // TODO mention replacing tooltip
        setToolTipText(Messages.RestoreCell_TT);
        this.cells = cells;
        // TODO explain "enabled"
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
       //TODO Explain "clearUserValue"
        for (Cell cell : cells)
            if (cell.isEdited())
                cell.clearUserValue();
    }
}
