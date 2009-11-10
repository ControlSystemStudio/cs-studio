package org.csstudio.alarm.table.ui.messagetable;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreferenceConstants;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Difference to {@link MessageTable} for log messages is the listener for acknowledges by the user
 * and the {@link AlarmMessageTableMessageSorter} sorter for the table.
 * 
 * @author jhatje
 * 
 */
public class AlarmMessageTable extends MessageTable {

    private static final String SECURITY_ID = "operating";

    public AlarmMessageTable(TableViewer viewer, String[] colNames, MessageList j) {
        super(viewer, colNames, j);
    }

    @Override
    void initializeMessageTable(String[] pureColumnNames) {

        _tableViewer.setLabelProvider(new AlarmMessageTableLabelProvider(pureColumnNames));

        _tableViewer.setComparator(new AlarmMessageTableMessageSorter(_tableViewer));

        ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(),
                JmsLogsPlugin.getDefault().getBundle().getSymbolicName());
        prefStore.addPropertyChangeListener(new IPropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(AlarmViewPreferenceConstants.LOG_ALARM_FONT)) {
                    Font font = CustomMediaFactory.getInstance().getFont(
                            new FontData(event.getNewValue().toString()));
                    _tableViewer.getTable().setFont(font);
                    _tableViewer.getTable().layout(true);
                }

            }
        });

        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, true);

        TableColumn[] columns = _table.getColumns();
        for (final TableColumn tableColumn : columns) {
            if (tableColumn.getText().equals("SEVERITY")) {
                tableColumn.removeSelectionListener(_selectionListenerMap.get("SEVERITY"));
                tableColumn.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
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
            public void handleEvent(Event event) {
            	if (_contentProvider.getMessageUpdatePause()) {
            		return;
            	}
                if (event.item instanceof TableItem && event.button == 0 && event.detail == 32) {
                    TableItem ti = (TableItem) event.item;
                    if (canExecute) {
                        if (ti.getChecked()) {
                            if (ti.getData() instanceof BasicMessage) {
                                List<AlarmMessage> msgList = new ArrayList<AlarmMessage>();
                                msgList.add(((AlarmMessage) event.item.getData())
                                        .copy(new AlarmMessage()));
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
                        Shell activeShell = Display.getCurrent().getActiveShell();
                        MessageDialog md = new MessageDialog(activeShell, "Authorization", null,
                                "Not Acknowledged!\n\rPermission denied.", MessageDialog.WARNING,
                                new String[] { "Ok" }, 0);
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

    protected void resetCheckedStatus() {
        TableItem[] tableItems = _table.getItems();
        for (TableItem tableItem : tableItems) {
            Object item = tableItem.getData();
            if (item instanceof BasicMessage) {
                BasicMessage messageItem = (BasicMessage) item;
                if (messageItem.getProperty("ACK").equalsIgnoreCase("TRUE")) {
                    tableItem.setChecked(true);
                }
            }
        }
        _tableViewer.refresh();
    }
}
