package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.model.Cell;
import org.eclipse.jface.action.Action;

/** Action that restores a cell's value to the original value,
 *  replacing what the user might have entered.
 *  @author Kay Kasemir
 */
public class RestoreCellAction extends Action
{   
    final private Cell cell;

    public RestoreCellAction(final Cell cell)
    {
        super("Restore Original Value");
        setToolTipText("Replace entered value with value of underlying PV");
        this.cell = cell;
        setEnabled(cell.isEdited());
    }

    @Override
    public void run()
    {
        cell.clearUserValue();
    }
}
