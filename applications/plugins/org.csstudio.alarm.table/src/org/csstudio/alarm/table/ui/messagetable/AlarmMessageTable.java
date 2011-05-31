package org.csstudio.alarm.table.ui.messagetable;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.auth.security.SecurityFacade;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Difference to {@link MessageTable} for log messages is the listener for acknowledges by the user
 * and the {@link AlarmMessageTableMessageSorter} sorter for the table.
 *
 * @author jhatje
 *
 */
public class AlarmMessageTable extends MessageTable {

    private static final String SECURITY_ID = "operating";

    public AlarmMessageTable(final TableViewer viewer, final String[] colNames, final AbstractMessageList j) {
        super(viewer, colNames, j);
    }

    @Override
    void initializeMessageTable(final String[] pureColumnNames) {

        _tableViewer.setLabelProvider(new AlarmMessageTableLabelProvider(pureColumnNames));

        _tableViewer.setComparator(new AlarmMessageTableMessageSorter(_tableViewer));

        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, true);

        final TableColumn[] columns = _table.getColumns();
        for (final TableColumn tableColumn : columns) {
            if (tableColumn.getText().equals("SEVERITY")) {
                tableColumn.removeSelectionListener(_selectionListenerMap.get("SEVERITY"));
                tableColumn.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        _table.setSortColumn(tableColumn);
                        _tableViewer
                                .setComparator(new AlarmMessageTableMessageSorter(_tableViewer));
                        _table.setSortDirection(SWT.DOWN);
                        // sorting sets the checked status of table items to false. So we have to
                        // reset it the previous checked status.
                        resetCheckedStatus();
                        return;
                    }
                });
                break;
            }
        }

        _table.addListener(SWT.Selection, new Listener() {
            public void handleEvent(final Event event) {
            	if (_contentProvider.getMessageUpdatePause()) {
            		return;
            	}
                if ((event.item instanceof TableItem) && (event.button == 0) && (event.detail == 32)) {
                    final TableItem ti = (TableItem) event.item;
                    if (canExecute) {
                        if (ti.getChecked()) {
                            if (ti.getData() instanceof BasicMessage) {
                                final List<AlarmMessage> msgList = new ArrayList<AlarmMessage>();
                                final AlarmMessage copy = ((AlarmMessage) event.item.getData()).copy();
                                msgList.add(copy);
                                final SendAcknowledge sendAck = SendAcknowledge
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
                        final Shell activeShell = Display.getCurrent().getActiveShell();
                        final MessageDialog md = new MessageDialog(activeShell, "Authorization", null,
                                "Not Acknowledged!\n\rPermission denied.", MessageDialog.WARNING,
                                new String[] { "Ok" }, 0);
                        md.open();
                    }
                    // Click on other columns but ack should not check or
                    // uncheck the ack box
                } else if ((event.item instanceof TableItem) && (event.button == 0)
                        && (event.detail == 0)) {
                    final TableItem ti = (TableItem) event.item;
                    if (!ti.getChecked()) {
                        ti.setChecked(false);
                    }
                }
            }
        });
    }

    @Override
    protected void resetCheckedStatus() {
        final TableItem[] tableItems = _table.getItems();
        for (final TableItem tableItem : tableItems) {
            final Object item = tableItem.getData();
            if (item instanceof BasicMessage) {
                final BasicMessage messageItem = (BasicMessage) item;
                final String ackProp = messageItem.getProperty(AlarmMessageKey.ACK.getDefiningName());
                if ((ackProp != null) && Boolean.valueOf(ackProp)) {
                    tableItem.setChecked(true);
                }
            }
        }
        _tableViewer.refresh();
    }
}
