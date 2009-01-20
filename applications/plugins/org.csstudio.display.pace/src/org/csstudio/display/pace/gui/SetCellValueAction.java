package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.Messages;
import org.csstudio.display.pace.model.Cell;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that sets several cells to the same user-supplied value
 *  @author Kay Kasemir
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
        if (cells == null)
            setEnabled(false);
    }

    @Override
    public void run()
    {
        final InputDialog input = new InputDialog(shell, Messages.SetValue_Title,
                Messages.SetValue_Msg,
                cells[0].getValue(), null);
        if (input.open() != InputDialog.OK)
            return;
        final String user_value = input.getValue();
        for (Cell cell : cells)
            cell.setUserValue(user_value);
    }
}
