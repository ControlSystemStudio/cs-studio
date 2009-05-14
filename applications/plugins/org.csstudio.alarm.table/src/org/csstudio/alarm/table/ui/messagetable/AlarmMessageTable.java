package org.csstudio.alarm.table.ui.messagetable;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * Difference to {@link MessageTable} for log messages is the listener for
 * acknowledges by the user and the {@link AlarmMessageTableAddMessageSorter}
 * sorter for the table.
 * 
 * @author jhatje
 * 
 */
public class AlarmMessageTable extends MessageTable {

    private static final String SECURITY_ID = "operating";

    public AlarmMessageTable(TableViewer viewer, String[] colNames,
            JMSMessageList j) {
        super(viewer, colNames, j);
    }

    @Override
    void initializeMessageTable() {

        _tableViewer.setSorter(new AlarmMessageTableAddMessageSorter());

        final boolean canExecute = SecurityFacade.getInstance().canExecute(
                SECURITY_ID, true);

        _table.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (event.item instanceof TableItem && event.button == 0
                        && event.detail == 32) {
                    TableItem ti = (TableItem) event.item;
                    if (canExecute) {
                        if (ti.getChecked()) {
                            if (ti.getData() instanceof JMSMessage) {
                                List<JMSMessage> msgList = new ArrayList<JMSMessage>();
                                msgList.add(((JMSMessage) event.item.getData())
                                        .copy());
                                SendAcknowledge sendAck = SendAcknowledge
                                        .newFromJMSMessage(msgList);
                                sendAck.schedule();
                            } else {
                                return;
                            }
                        } else {
                            ti.setChecked(true);
                        }
                    } else {
                        ti.setChecked(false);
                        Shell activeShell = Display.getCurrent()
                                .getActiveShell();
                        MessageDialog md = new MessageDialog(activeShell,
                                "Authorization", null,
                                "Not Acknowledged!\n\rPermission denied.",
                                MessageDialog.WARNING, new String[] { "Ok" }, 0);
                        md.open();
                    }
                    // Click on other columns but ack should not check or
                    // uncheck the ack box
                } else if (event.item instanceof TableItem && event.button == 0
                        && event.detail == 0) {
                    TableItem ti = (TableItem) event.item;
                    if (ti.getChecked() == false) {
                        ti.setChecked(false);
                    }
                }
            }
        });

    }
}
