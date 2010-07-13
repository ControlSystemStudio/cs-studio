package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** CellLabelProvider that fills cells with sequence number of Message.
 *  @author Kay Kasemir
 */
public class SeqProvider extends CellLabelProvider
{
    /** Show "Sequence: ..." as tool-tip */
    @Override
	public String getToolTipText(final Object element)
    {
        final Message message = (Message) element;
        return String.format("Sequence number: %s", message.getSequence());
	}

    @Override
    public void update(ViewerCell cell)
    {
        final Message message = (Message) cell.getElement();
        cell.setText(Integer.toString(message.getSequence()));
    }
}
