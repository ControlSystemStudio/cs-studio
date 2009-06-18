/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.ui.messagetable;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Holding {@link TableViewer} that displays messages from {@link MessageList}.
 * Initializes table columns, set context menu and sorter.
 * 
 * @author jhatje
 * 
 */
public class MessageTable {

    Table _table;

    TableViewer _tableViewer;

    TableColumn[] _tableColumn;

    String[] columnHeader;

    String colName = null;

    String columnSelection = null;

    private String lastSort = "";

    private boolean sort = false;

    private MessageList _messageList;

    int[] columnWidth;

    Map<String, SelectionAdapter> _selectionListenerMap = new HashMap<String, SelectionAdapter>();

    public MessageTable(TableViewer tViewer, String[] colNames, MessageList j) {

        _tableViewer = tViewer;
        _table = _tableViewer.getTable();
        _messageList = j;

        _table.setHeaderVisible(true);
        _table.setLinesVisible(true);

        // Get back column names without width
        String[] pureColumnNames = setTableColumns(colNames);

        _tableViewer.setContentProvider(new MessageTableContentProvider(
                _tableViewer, _messageList));

        _tableViewer.setInput(_messageList);

        initializeMessageTable(pureColumnNames);

        new ProcessVariableDragSource(_tableViewer.getTable(), _tableViewer);
    }

    /**
     * Initialize table with content-, label provider, sorter and input
     * 
     * @param colNames
     */
    void initializeMessageTable(String[] pureColumnNames) {

        _tableViewer.setLabelProvider(new MessageTableLabelProvider(
                pureColumnNames));
        _tableViewer.setComparator(new MessageTableMessageSorter(_tableViewer));

    }

    private String[] setTableColumns(String[] colNames) {
        columnWidth = new int[colNames.length];
        columnHeader = colNames;

        _table
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                        1));
        String[] columnName = new String[columnHeader.length];
        _tableColumn = new TableColumn[colNames.length];
        for (int i = 0; i < columnHeader.length; i++) {
            _tableColumn[i] = new TableColumn(_table, SWT.CENTER);
            String[] temp = columnHeader[i].split(",");
            colName = temp[0];
            columnName[i] = colName;
            _tableColumn[i].setText(temp[0]);
            if (temp.length == 2) {
                _tableColumn[i].setWidth(Integer.parseInt(columnHeader[i]
                        .split(",")[1]));
            } else
                _tableColumn[i].setWidth(100);
            final int j = i;
            SelectionAdapter columnSelectionListener = new SelectionAdapter() {

                public String cName = colName;
                private TableColumn column = _tableColumn[j];

                public void widgetSelected(SelectionEvent e) {
                    _table.setSortColumn(column);
                    if (cName.equals(lastSort)) {
                        sort = !sort;
                        _tableViewer
                                .setComparator(new MessageTableColumnSorter(
                                        _tableViewer, cName, sort));
                    } else {
                        sort = false;
                        _tableViewer
                                .setComparator(new MessageTableColumnSorter(
                                        _tableViewer, cName, sort));
                    }
                    if (sort) {
                        _table.setSortDirection(SWT.DOWN);
                    } else {
                        _table.setSortDirection(SWT.UP);
                    }
                    lastSort = cName;
                }
            };
            _tableColumn[i].addSelectionListener(columnSelectionListener);
            _selectionListenerMap.put(colName, columnSelectionListener);
        }

        return columnName;
    }

    public void makeContextMenu(IWorkbenchPartSite site) {
        MenuManager manager = new MenuManager("#PopupMenu");
        Control contr = _tableViewer.getControl();
        manager.add(new ShowMessagePropertiesAction(_tableViewer));

        manager.add(new DeleteMessageAction(_tableViewer, _messageList));
        manager.add(new DeleteAllMessagesAction(_tableViewer, _messageList));

        manager.add(new Separator());
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new Separator(
                        IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        // getSelectedColumn(contr);
        Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        site.registerContextMenu(manager, _tableViewer);
    }

    /**
     * Identify the selected column by mouse position. We do not need it now,
     * but later it is maybe useful.
     * 
     * @param contr
     */
    private void getSelectedColumn(Control contr) {
        contr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                super.mouseDown(e);
                if (e.button == 3) {
                    MessageTableLabelProvider lablProvider = (MessageTableLabelProvider) _tableViewer
                            .getLabelProvider();

                    Table t = _tableViewer.getTable();
                    TableItem ti = t.getItem(new Point(e.x, e.y));
                    for (int i = 0; i < MessageTable.this.columnHeader.length; i++) {
                        Rectangle bounds = ti.getBounds(i);
                        if (bounds.contains(e.x, e.y)) {
                            columnSelection = lablProvider.getColumnName(i);
                            break;
                        }
                    }
                }
            }
        });
    }
}