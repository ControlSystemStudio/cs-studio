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

import javax.annotation.Nonnull;

import org.csstudio.config.authorizeid.SortActionFactory.TypedComparator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

enum GroupRoleTableViewerFactory {
    
    INSTANCE;
    
    enum GroupRoleTableColumns {
        GROUP, ROLE, USER
    }
    
    /**
     * @param parent a composite
     */
    @Nonnull
    public TableViewer createTableViewer(@Nonnull final Composite parent) {
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        parent.setLayout(tableColumnLayout);
        
        TableViewer groupRoleTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        
        final Table groupRoleTable = groupRoleTableViewer.getTable();
        groupRoleTable.setHeaderVisible(true);
        groupRoleTable.setLinesVisible(true);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 200;
        groupRoleTable.setLayoutData(gridData);
        
        // watch out for correct sequence
        createGroupColumn(groupRoleTableViewer, tableColumnLayout);
        createRoleColumn(groupRoleTableViewer, tableColumnLayout);
        createUsersColumn(groupRoleTableViewer, tableColumnLayout);
        
        // Consistency check
        assert groupRoleTable.getColumnCount() == GroupRoleTableColumns.values().length;
        
        groupRoleTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        groupRoleTableViewer.setLabelProvider(new GroupRoleLabelProvider());
        
        return groupRoleTableViewer;
    }
    
    @Nonnull
    private TableColumn createGroupColumn(@Nonnull final TableViewer viewer,
                                          @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIG);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH));
        SortActionFactory.connectSortActionToColumn(viewer, column, createGroupComparator(), true);
        return column;
    }
    
    private TypedComparator<GroupRoleTableEntry> createGroupComparator() {
        return new TypedComparator<GroupRoleTableEntry>() {
            @Override
            public int compare(GroupRoleTableEntry entry1, GroupRoleTableEntry entry2) {
                return SortActionFactory.robustStringCompare(entry1.getEaig(), entry2.getEaig());
            }
        };
    }
    
    @Nonnull
    private TableColumn createRoleColumn(@Nonnull final TableViewer viewer,
                                         @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIR);
        
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH));
        SortActionFactory.connectSortActionToColumn(viewer, column, createRoleComparator(), false);
        return column;
    }
    
    private TypedComparator<GroupRoleTableEntry> createRoleComparator() {
        return new TypedComparator<GroupRoleTableEntry>() {
            @Override
            public int compare(GroupRoleTableEntry entry1, GroupRoleTableEntry entry2) {
                return SortActionFactory.robustStringCompare(entry1.getEair(), entry2.getEair());
            }
        };
    }
    
    @Nonnull
    private TableColumn createUsersColumn(@Nonnull final TableViewer viewer,
                                          @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_USERS);
        
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(80, ColumnWeightData.MINIMUM_WIDTH));
        return column;
    }
}