package org.csstudio.debugging.jmsmonitor;

import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.platform.logging.JMSLogMessage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/** Dialog to display detail of messages.
 *  @author Kay Kasemir
 */
public class MessageDetailDialog extends Dialog
{
    /** The messages to display */
    final private ReceivedMessage[] messages;

    /** Initialize & run
     *  @param shell Parent shell
     *  @param messages Messages to display
     */
    public MessageDetailDialog(final Shell shell, final ReceivedMessage[] messages)
    {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.messages = messages;
    }

    /** Create table that lists all properties: Name & value */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        parent.getShell().setText(Messages.DetailDialogTitle);
        final Composite composite = (Composite) super.createDialogArea(parent);
        //add controls to composite as necessary

        final Table table = new Table(composite,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        AutoSizeColumn.make(table, Messages.Property, 150, 10);
        AutoSizeColumn.make(table, Messages.Value, 500, 100);
        new AutoSizeControlListener(table.getParent(), table);
        
        boolean first = true;
        for (ReceivedMessage message : messages)
        {
            TableItem item;
            if (first)
                first = false;
            else
            {
                item = new TableItem (table, SWT.NONE);
                item.setText (0, Messages.SeperatorType);
                item.setText (1, Messages.SeparatorValue);
            }
            item = new TableItem (table, SWT.NONE);
            item.setText (0, JMSLogMessage.TYPE);
            item.setText (1, message.getType());
            for (int i=0; i<message.getPropertyCount(); ++i)
            {
                final MessageProperty prop = message.getProperty(i);
                item = new TableItem (table, SWT.NONE);
                item.setText (0, prop.getName());
                item.setText (1, prop.getValue());
            }
        }
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        
        return composite;
    }

    /** Create only the 'OK' button,
     *  not the 'Cancel' that would come with the default implementation
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }
}
