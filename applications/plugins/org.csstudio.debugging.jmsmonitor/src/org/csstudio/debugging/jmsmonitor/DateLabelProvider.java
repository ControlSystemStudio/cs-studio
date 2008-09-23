package org.csstudio.debugging.jmsmonitor;

import java.text.SimpleDateFormat;

import org.csstudio.platform.logging.JMSLogMessage;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Cell label provide that puts message date into cell.
 *  @author Kay Kasemir
 */
public class DateLabelProvider extends CellLabelProvider
{
    final private static SimpleDateFormat date_format =
        new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);

    @Override
    public void update(ViewerCell cell)
    {
        // ReceivedMessageProvider should always provide "ReceivedMessage" elements
        final ReceivedMessage msg = (ReceivedMessage) cell.getElement();
        cell.setText(date_format.format(msg.getDate()));
        
    }
}
