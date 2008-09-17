package org.csstudio.debugging.jmsmonitor;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Cell label provide that puts message content into cell.
 *  @author Kay Kasemir
 */
public class ContentLabelProvider extends CellLabelProvider
{
    @Override
    public void update(ViewerCell cell)
    {
        // ReceivedMessageProvider should always provide "ReceivedMessage" elements
        final ReceivedMessage msg = (ReceivedMessage) cell.getElement();
        cell.setText(msg.getContent());
    }
}
