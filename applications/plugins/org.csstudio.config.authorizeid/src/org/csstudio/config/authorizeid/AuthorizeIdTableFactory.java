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
import java.util.Collection;
import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.platform.security.RegisteredAuthorizationId;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class AuthorizeIdTableFactory {
    
    /**
     * @param parent a composite
     */
    public static TableViewer createAuthorizeIdTableViewer(final Composite parent) {
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

        
        createAuthorizeIdColumn(authorizeIdTableViewer, tableColumnLayout);
        createIsRegisteredColumn(authorizeIdTableViewer, tableColumnLayout);
        createDescriptionColumn(authorizeIdTableViewer, tableColumnLayout);
        createOriginatingPluginColumn(authorizeIdTableViewer, tableColumnLayout);
        
        authorizeIdTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        authorizeIdTableViewer.setLabelProvider(new AuthorizeIdLabelProvider());
        
        return authorizeIdTableViewer;
    }
    
    private static TableColumn createAuthorizeIdColumn(@Nonnull final TableViewer viewer,
                                                       @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column =  tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_EAIN);
        tableColumnLayout.setColumnData(column, new ColumnWeightData(15, ColumnWeightData.MINIMUM_WIDTH));
        
        column.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                // sort column 1
                TableItem[] items = viewer.getTable().getItems();
                final Collator collator = Collator.getInstance(Locale.getDefault());
                for (int i = 1; i < items.length; i++) {
                    final String value1 = items[i].getText(0);
                    for (int j = 0; j < i; j++) {
                        final String value2 = items[j].getText(0);
                        if (collator.compare(value1, value2) < 0) {
                            final String[] values = { items[i].getText(0), items[i].getText(1) };
                            items[i].dispose();
                            final TableItem item = new TableItem(viewer.getTable(), SWT.NONE, j);
                            item.setText(values);
                            items = viewer.getTable().getItems();
                            break;
                        }
                    }
                }
            }
        });
        
        return column;
    }
    
    private static TableColumn createIsRegisteredColumn(@Nonnull final TableViewer viewer,
                                                        @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column =  tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_IS_REGISTERED);
        tableColumnLayout.setColumnData(column, new ColumnWeightData(15, ColumnWeightData.MINIMUM_WIDTH));
        return column;
    }
    
    private static TableColumn createDescriptionColumn(@Nonnull final TableViewer viewer,
                                                       @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column =  tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_DESCRIPTION);
        tableColumnLayout.setColumnData(column, new ColumnWeightData(40, ColumnWeightData.MINIMUM_WIDTH));
        return column;
    }
    
    private static TableColumn createOriginatingPluginColumn(@Nonnull final TableViewer viewer,
                                                             @Nonnull final TableColumnLayout tableColumnLayout) {
        final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
        final TableColumn column =  tableViewerColumn.getColumn();
        column.setText(Messages.AuthorizeIdView_ORIGINATING_PLUGIN);
        tableColumnLayout.setColumnData(column, new ColumnWeightData(30, ColumnWeightData.MINIMUM_WIDTH));
        return column;
    }
    
    private static class AuthorizeIdLabelProvider extends LabelProvider implements
            ITableLabelProvider {
        
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        
        @Override
        public String getColumnText(Object element, int columnIndex) {
            String result = null;
            String authorizationId = element.toString();
            
            switch (columnIndex) {
                case 0:
                    result = authorizationId;
                    break;
                case 1:
                    result = getIsRegisteredAsExtension(authorizationId);
                    break;
                case 2:
                    result = getDescription(authorizationId);
                    break;
                case 3:
                    result = getOriginatingPlugin(authorizationId);
                    break;
                default:
                    result = null;
            }
            return result;
        }
        
        @Nonnull
        private String getIsRegisteredAsExtension(@Nonnull final String authorizationId) {
            RegisteredAuthorizationId registeredAuthorizationId = getRegisteredAuthorizationId(authorizationId);
            return registeredAuthorizationId == null ? "no" : "yes";
        }
        
        @CheckForNull
        private RegisteredAuthorizationId getRegisteredAuthorizationId(@Nonnull final String authorizationId) {
            RegisteredAuthorizationId result = null;
            Collection<RegisteredAuthorizationId> authIds = SecurityFacade.getInstance()
                    .getRegisteredAuthorizationIds();
            for (RegisteredAuthorizationId registeredAuthorizationId : authIds) {
                if (registeredAuthorizationId.getId().equals(authorizationId)) {
                    result = registeredAuthorizationId;
                    break;
                }
            }
            return result;
        }
        
        private String getDescription(String authorizationId) {
            RegisteredAuthorizationId registeredAuthorizationId = getRegisteredAuthorizationId(authorizationId);
            return registeredAuthorizationId == null ? null : registeredAuthorizationId
                    .getDescription();
        }
        
        private String getOriginatingPlugin(String authorizationId) {
            RegisteredAuthorizationId registeredAuthorizationId = getRegisteredAuthorizationId(authorizationId);
            return registeredAuthorizationId == null ? null : "unknown";
        }
        
    }
}