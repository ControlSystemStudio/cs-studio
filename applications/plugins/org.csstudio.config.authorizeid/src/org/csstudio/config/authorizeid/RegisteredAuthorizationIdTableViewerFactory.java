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

import org.csstudio.auth.security.RegisteredAuthorizationId;
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

enum RegisteredAuthorizationIdTableViewerFactory {
    
    INSTANCE;
    
    enum RegisteredAuthorizationIdTableColumns {
        AUTH_ID, DESCRIPTION, ORIGINATING_PLUGIN
    }
    
    /**
     * @param parent a composite
     */
    @Nonnull
    public TableViewer createTableViewer(@Nonnull final Composite parent) {
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        parent.setLayout(tableColumnLayout);
        
        TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.BORDER);
        
        Table _table = tableViewer.getTable();
        _table.setHeaderVisible(true);
        _table.setLinesVisible(true);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 100;
        _table.setLayoutData(gridData);
        
        // watch out for correct sequence
        createAuthorizeIdColumn(tableViewer, tableColumnLayout);
        createDescriptionColumn(tableViewer, tableColumnLayout);
        createOriginatingPluginColumn(tableViewer, tableColumnLayout);
        
        // Consistency check
        assert _table.getColumnCount() == RegisteredAuthorizationIdTableColumns.values().length;
        
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setLabelProvider(new RegisteredAuthorizeIdLabelProvider());
        
        return tableViewer;
    }
    
    @Nonnull
    private TableColumn createAuthorizeIdColumn(@Nonnull final TableViewer viewer,
                                                @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIN);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(15, ColumnWeightData.MINIMUM_WIDTH));
        SortActionFactory.connectSortActionToColumn(viewer, column, createAuthorizeIdComparator(), true);
        return column;
    }
    
    private TypedComparator<RegisteredAuthorizationId> createAuthorizeIdComparator() {
        return new TypedComparator<RegisteredAuthorizationId>() {
            @Override
            public int compare(RegisteredAuthorizationId entry1, RegisteredAuthorizationId entry2) {
                return entry1.getId().compareTo(entry2.getId());
            }
        };
    }
    
    @Nonnull
    private TableColumn createDescriptionColumn(@Nonnull final TableViewer viewer,
                                                @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_DESCRIPTION);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(50, ColumnWeightData.MINIMUM_WIDTH));
        SortActionFactory.connectSortActionToColumn(viewer, column, createDescriptionComparator(), false);
        return column;
    }
    
    private TypedComparator<RegisteredAuthorizationId> createDescriptionComparator() {
        return new TypedComparator<RegisteredAuthorizationId>() {
            @Override
            public int compare(RegisteredAuthorizationId entry1, RegisteredAuthorizationId entry2) {
                return entry1.getDescription().compareTo(entry2.getDescription());
            }
        };
    }

    @Nonnull
    private TableColumn createOriginatingPluginColumn(@Nonnull final TableViewer viewer,
                                                      @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_ORIGINATING_PLUGIN);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(35, ColumnWeightData.MINIMUM_WIDTH));
        SortActionFactory.connectSortActionToColumn(viewer, column, createOriginatingPluginComparator(), false);
        return column;
    }

    private TypedComparator<RegisteredAuthorizationId> createOriginatingPluginComparator() {
        return new TypedComparator<RegisteredAuthorizationId>() {
            @Override
            public int compare(RegisteredAuthorizationId entry1, RegisteredAuthorizationId entry2) {
                return entry1.getContributor().compareTo(entry2.getContributor());
            }
        };
    }


}