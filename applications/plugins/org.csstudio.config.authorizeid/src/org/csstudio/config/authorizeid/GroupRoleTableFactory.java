/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.config.authorizeid;

import java.text.Collator;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class GroupRoleTableFactory {
    
    private static Table _groupRoleTable;
    
    /**
     * @param parent a composite
     */
    public static TableViewer createGroupRoleTableViewer(final Composite parent) {
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        parent.setLayout(tableColumnLayout);
        
        TableViewer groupRoleTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        
        _groupRoleTable = groupRoleTableViewer.getTable();
        _groupRoleTable.setHeaderVisible(true);
        _groupRoleTable.setLinesVisible(true);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 200;
        _groupRoleTable.setLayoutData(gridData);

        createGroupColumn(groupRoleTableViewer, tableColumnLayout);
        createRoleColumn(groupRoleTableViewer, tableColumnLayout);
        createUsersColumn(groupRoleTableViewer, tableColumnLayout);
        
        groupRoleTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        groupRoleTableViewer.setLabelProvider(new GroupRoleLabelProvider());

        return groupRoleTableViewer;
    }

    private static TableColumn createGroupColumn(@Nonnull final TableViewer viewer,
                                                 @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIG);
        tableColumnLayout
                .setColumnData(column, new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH));
        column.addListener(SWT.Selection, new MyListener(0));
        return column;
    }

    private static TableColumn createRoleColumn(@Nonnull final TableViewer viewer,
                                                @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIR);
        
        tableColumnLayout
                .setColumnData(column, new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH));
        column.addListener(SWT.Selection, new MyListener(1));
        return column;
    }
    
    private static TableColumn createUsersColumn(@Nonnull final TableViewer viewer,
                                                @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_USERS);
        
        tableColumnLayout
                .setColumnData(column, new ColumnWeightData(80, ColumnWeightData.MINIMUM_WIDTH));
        return column;
    }
    
    
    
    /**
     * Listener for sorting columns for second table.
     */
    private static class MyListener implements Listener {
        
        private final int i;
        
        public MyListener(final int i) {
            super();
            this.i = i;
        }
        
        @Override
        public void handleEvent(final Event event) {
            sortColumn(i);
        }
    }
    
    /**
     * Sorts column alphabetically, when clicking on it's "header".
     * @param colNum the number of column in table (starts with 0)
     */
    private static void sortColumn(final int colNum) {
        TableItem[] items = _groupRoleTable.getItems();
        final Collator collator = Collator.getInstance(Locale.getDefault());
        for (int i = 1; i < items.length; i++) {
            final String value1 = items[i].getText(colNum);
            for (int j = 0; j < i; j++) {
                final String value2 = items[j].getText(colNum);
                if (collator.compare(value1, value2) < 0) {
                    final String[] values = { items[i].getText(0), items[i].getText(1) };
                    items[i].dispose();
                    final TableItem item = new TableItem(_groupRoleTable, SWT.NONE, j);
                    item.setText(values);
                    items = _groupRoleTable.getItems();
                    break;
                }
            }
        }
    }
    
}