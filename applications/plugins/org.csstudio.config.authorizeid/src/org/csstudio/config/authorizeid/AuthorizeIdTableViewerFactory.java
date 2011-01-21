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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

enum AuthorizeIdTableViewerFactory {
    
    INSTANCE;
    
    enum AuthorizeIdTableColumns {
        AUTH_ID, DESCRIPTION, REGISTERED_AS_EXTENSION
    }
    
    /**
     * @param parent a composite
     */
    @Nonnull
    public TableViewer createTableViewer(@Nonnull final Composite parent) {
        final TableViewer authorizeIdTableViewer = new TableViewer(parent, SWT.SINGLE
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        parent.setLayout(tableColumnLayout);
        
        Table authorizeIdTable = authorizeIdTableViewer.getTable();
        authorizeIdTable.setHeaderVisible(true);
        authorizeIdTable.setLinesVisible(true);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 200;
        authorizeIdTable.setLayoutData(gridData);
        
        // watch out for correct sequence
        createAuthorizeIdColumn(authorizeIdTableViewer, tableColumnLayout);
        createDescriptionColumn(authorizeIdTableViewer, tableColumnLayout);
        createIsRegisteredColumn(authorizeIdTableViewer, tableColumnLayout);
        
        // Consistency check
        assert authorizeIdTable.getColumnCount() == AuthorizeIdTableColumns.values().length;
        
        authorizeIdTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        authorizeIdTableViewer.setLabelProvider(new AuthorizeIdLabelProvider());
        
        return authorizeIdTableViewer;
    }
    
    @Nonnull
    private TableColumn createAuthorizeIdColumn(@Nonnull final TableViewer viewer,
                                                       @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIN);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(20, ColumnWeightData.MINIMUM_WIDTH));

        connectSortActionToColumn(viewer, column, createAuthorizeIdComparator());

        return column;
    }

    @Nonnull
    private TableColumn createDescriptionColumn(@Nonnull final TableViewer viewer,
                                                @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_DESCRIPTION);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(70, ColumnWeightData.MINIMUM_WIDTH));
        connectSortActionToColumn(viewer, column, createDescriptonComparator());
        return column;
    }

    @Nonnull
    private TableColumn createIsRegisteredColumn(@Nonnull final TableViewer viewer,
                                                 @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_IS_REGISTERED);
        tableColumnLayout.setColumnData(column,
                                        new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH));
        connectSortActionToColumn(viewer, column, createDescriptonComparator());
        return column;
    }
    
    private void connectSortActionToColumn(@Nonnull final TableViewer viewer,
                                           @Nonnull final TableColumn column,
                                           @Nonnull final InvertableViewerComparator comparator) {
        final Action action = new Action() {
            @Override
            public void run() {
                viewer.getTable().setSortColumn(column);
                comparator.invertSortDirection();
                viewer.getTable().setSortDirection(comparator.getSortDirection() ? SWT.UP
                        : SWT.DOWN);
                viewer.setComparator(comparator);
                viewer.refresh();
            }
        };
        
        SelectionListener listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.run();
            }
        };
        column.addSelectionListener(listener);
    }

    private static interface IAuthorizeIdComparator {
        int compare(AuthorizedIdTableEntry entry1, AuthorizedIdTableEntry entry2);
    }

    private InvertableViewerComparator createAuthorizeIdComparator() {
        return createComparator(new IAuthorizeIdComparator() {
            @Override
            public int compare(AuthorizedIdTableEntry entry1, AuthorizedIdTableEntry entry2) {
                return entry1.getAuthorizeId().compareTo(entry2.getAuthorizeId());
            }
        });
    }

    private InvertableViewerComparator createDescriptonComparator() {
        return createComparator(new IAuthorizeIdComparator() {
            @Override
            public int compare(AuthorizedIdTableEntry entry1, AuthorizedIdTableEntry entry2) {
                return robustStringCompare(entry1.getDescription(), entry2.getDescription());
            }
        });
    }

    int robustStringCompare(@CheckForNull final String string1, @CheckForNull final String string2) {
        int result = 0;
        if ((string1 == null) && (string2 == null)) {
            result = 0;
        } else if (string1 == null) {
            result = -1;
        }
        else if (string2 == null) {
            result = 1;
        } else {
            result = string1.compareTo(string2);
        }
        return result;
    }
    
    
    private static class InvertableViewerComparator extends ViewerComparator {
        private boolean up = false;

        void invertSortDirection() {
            up = !up;
        }

        public boolean getSortDirection() {
            return up;
        }
    }

    
    @SuppressWarnings("synthetic-access")
    private InvertableViewerComparator createComparator(final IAuthorizeIdComparator comparator) {
        return new InvertableViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                // guard against wrong element types
                if (!((e1 instanceof AuthorizedIdTableEntry) && (e2 instanceof AuthorizedIdTableEntry))) {
                    return 0;
                }

                int result = comparator.compare((AuthorizedIdTableEntry) e1, (AuthorizedIdTableEntry) e2);
                result = getSortDirection() ? result : -result;
                return result;
            }
        };
    }

    
}