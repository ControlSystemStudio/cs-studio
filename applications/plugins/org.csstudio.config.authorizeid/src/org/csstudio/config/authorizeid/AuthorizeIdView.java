package org.csstudio.config.authorizeid;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;

import org.apache.log4j.Logger;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdGRManagement;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdManagement;
import org.csstudio.config.authorizeid.ldap.LdapAccess;
import org.csstudio.config.authorizeid.ldap.ObjectClass1;
import org.csstudio.config.authorizeid.ldap.ObjectClass2;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.RegisteredAuthorizationId;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Tool to display and edit ldap content of authorization ID data
 * @author Rok Povsic / Jörg Penning
 */
public class AuthorizeIdView extends ViewPart {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AuthorizeIdView.class);
    
    
    public static final String ID = "org.csstudio.config.authorizeid";//$NON-NLS-1$
    
    private Label categoryLabel;
    private Combo _categoryCombo;
    
    private TableViewer _authorizeIdTableViewer;
    private Table _authorizeIdTable;
    
    private TableViewer _groupRoleTableViewer;
    private Table _groupRoleTable;
    
    //    private static final String SECURITY_ID = "AuthorizeId";
    private static final String SECURITY_ID = "remoteManagement";
    
    /**
     * Creates a view for the plugin.
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, true));
        
        final Composite authIDPanel = new Composite(parent, SWT.FILL);
        authIDPanel.setLayout(new GridLayout(3, false));
        authIDPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite groupRolePanel = new Composite(parent, SWT.FILL);
        groupRolePanel.setLayout(new GridLayout(2, false));
        groupRolePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createAuthIdPanel(authIDPanel);
        createGroupRolePanel(groupRolePanel);
    }
    
    private void createAuthIdPanel(@Nonnull final Composite authIDPanel) {
        final Group categoryGroupPanel = new Group(authIDPanel, SWT.NONE);
        categoryGroupPanel.setText(Messages.AuthorizeIdView_SELECT_GROUP);
        categoryGroupPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
        categoryGroupPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        
        categoryLabel = new Label(categoryGroupPanel, SWT.CENTER);
        categoryLabel.setText(Messages.AuthorizeIdView_GROUP);
        
        _categoryCombo = new Combo(categoryGroupPanel, SWT.NONE | SWT.READ_ONLY);
        
        String[] groups = new String[] { Messages.AuthorizeIdView_MessageWrong1 };
        
        try {
            groups = LdapAccess.getGroups();
        } catch (final Exception e) {
            // TODO (jpenning) popup with error message else user will not notice
            LOG.warn("No groups found in LDAP", e);
        }
        
        for (final String group : groups) {
            _categoryCombo.add(group);
        }
        
        _categoryCombo.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(final SelectionEvent e) {
                refreshAuthorizeIdTable();
                _groupRoleTable.removeAll();
                _groupRoleTable.clearAll();
            }
            
        });

        final Composite authIdTablePanel = new Composite(authIDPanel, SWT.NONE);
        authIdTablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _authorizeIdTableViewer = AuthorizeIdTableFactory
                .createAuthorizeIdTableViewer(authIdTablePanel);
        _authorizeIdTable = _authorizeIdTableViewer.getTable();
        _authorizeIdTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                refreshGroupRoleTable();
            }
        });
        
        final Composite authIdButtonPanel = new Composite(authIDPanel, SWT.NONE);
        authIdButtonPanel.setLayout(new FillLayout(SWT.VERTICAL));
        authIdButtonPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        createButtons1(authIdButtonPanel);
    }
    
    
    private void createGroupRolePanel(@Nonnull final Composite groupRolePanel) {
        final Composite groupRoleTablePanel = new Composite(groupRolePanel, SWT.NONE);
        groupRoleTablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _groupRoleTableViewer = GroupRoleTableFactory.createGroupRoleTableViewer(groupRoleTablePanel);
        _groupRoleTable = _groupRoleTableViewer.getTable();
        _groupRoleTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Composite groupRoleButtonPanel = new Composite(groupRolePanel, SWT.NONE);
        groupRoleButtonPanel.setLayout(new FillLayout(SWT.VERTICAL));
        createButtons2(groupRoleButtonPanel);
    }

    
    /**
     * Creates first set of buttons.
     * @param parenta composite
     */
    private void createButtons1(final Composite parent) {
        /**
         * "New" button for the first table.
         */
        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, false);
        final Button _new = new Button(parent, SWT.PUSH);
        _new.setText(Messages.AuthorizeIdView_NEW);
        _new.setEnabled(canExecute);
        _new.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (_categoryCombo.getText().equals("")) { //$NON-NLS-1$
                    final Status status = new Status(IStatus.ERROR,
                                                     Messages.AuthorizeIdView_Error,
                                                     0,
                                                     Messages.AuthorizeIdView_InvalidGroup,
                                                     null);
                    
                    ErrorDialog.openError(Display.getCurrent().getActiveShell(),
                                          Messages.AuthorizeIdView_GroupError,
                                          Messages.AuthorizeIdView_GroupErrorDesc,
                                          status);
                } else {
                    final InputDialog dialog = new InputDialog(Display.getCurrent()
                                                                       .getActiveShell(),
                                                               Messages.AuthorizeIdView_NEW,
                                                               Messages.AuthorizeIdView_Name,
                                                               "", //$NON-NLS-1$
                                                               new NewDataValidator());
                    if (dialog.open() == Window.OK) {
                        
                        final String _name = dialog.getValue();
                        final String _group = _categoryCombo.getText();
                        
                        final ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;
                        
                        final AuthorizationIdManagement nd = new AuthorizationIdManagement();
                        try {
                            nd.insertNewData(_name, _group, oclass);
                        } catch (final ServiceUnavailableException e1) {
                            MessageDialog
                                    .openError(getSite().getShell(),
                                               "LDAP error.",
                                               "LDAP service unavailable, try again later or start the LDAP service manually.");
                            return;
                        } catch (final InvalidNameException e1) {
                            MessageDialog.openError(getSite().getShell(),
                                                    "LDAP naming error.",
                                                    "LDAP action failed.\n" + e1.getMessage());
                            return;
                        }
                        
                        refreshAuthorizeIdTable();
                    }
                }
            }
        });
        
        /**
         * "Edit" button for the first table.
         */
        final Button _edit = new Button(parent, SWT.PUSH);
        _edit.setText(Messages.AuthorizeIdView_EDIT);
        _edit.setEnabled(canExecute);
        _edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String name = _authorizeIdTable.getSelection()[0].getText();
                final String _group = _categoryCombo.getText();
                
                final InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(),
                                                           Messages.AuthorizeIdView_EDIT,
                                                           Messages.AuthorizeIdView_NameEdit,
                                                           name,
                                                           new NewDataValidator());
                if (dialog.open() == Window.OK) {
                    
                    final String _name = dialog.getValue();
                    
                    final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;
                    final AuthorizationIdGRManagement ndGr = new AuthorizationIdGRManagement();
                    
                    final ArrayList<String> eaig = new ArrayList<String>();
                    final ArrayList<String> eair = new ArrayList<String>();
                    
                    try {
                        for (int i = 0; i < _groupRoleTable.getItemCount(); i++) {
                            eaig.add(_groupRoleTable.getItem(i).getText(0));
                            eair.add(_groupRoleTable.getItem(i).getText(1));
                            
                            ndGr.deleteData(name, eair.get(i), eaig.get(i), _group);
                        }
                        
                        final ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;
                        
                        final AuthorizationIdManagement nd = new AuthorizationIdManagement();
                        nd.deleteData(name, _group);
                        nd.insertNewData(_name, _group, oclass);
                        
                        for (int i = 0; i < _groupRoleTable.getItemCount(); i++) {
                            
                            ndGr.insertNewData(_name, _group, oclass2, eair.get(i), eaig.get(i));
                        }
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog
                                .openError(getSite().getShell(),
                                           "LDAP error.",
                                           "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
                    
                    refreshAuthorizeIdTable();
                }
                
            }
        });
        
        /**
         * "Delete" button for the first table.
         */
        final Button _delete = new Button(parent, SWT.PUSH);
        _delete.setText(Messages.AuthorizeIdView_DELETE);
        _delete.setEnabled(canExecute);
        _delete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                
                final String _name = _authorizeIdTable.getSelection()[0].getText();
                final String _group = _categoryCombo.getText();
                
                final MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
                                                             SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                messageBox.setMessage(Messages.AuthorizeIdView_DelWarn
                        + Messages.AuthorizeIdView_DelWarn2);
                messageBox.setText(Messages.AuthorizeIdView_DelEntry);
                final int response = messageBox.open();
                if (response == SWT.YES) {
                    
                    deleteWholeGroupRoleTable(_name, _group);
                    
                    final AuthorizationIdManagement aim = new AuthorizationIdManagement();
                    try {
                        aim.deleteData(_name, _group);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog
                                .openError(getSite().getShell(),
                                           "LDAP error.",
                                           "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
                    
                    refreshAuthorizeIdTable();
                    refreshGroupRoleTable();
                }
            }
        });
    }
    
    /**
     * Creates second set of buttons.
     *
     * @param parent
     *            a composite
     */
    private void createButtons2(final Composite parent) {
        /**
         * "New" button for second table.
         */
        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, false);
        final Button _new = new Button(parent, SWT.PUSH);
        _new.setText(Messages.AuthorizeIdView_NEW);
        _new.setEnabled(canExecute);
        _new.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String _name = _authorizeIdTable.getSelection()[0].getText();
                final CustomInputDialog dialog = new CustomInputDialog(Display.getCurrent()
                                                                               .getActiveShell(),
                                                                       Messages.AuthorizeIdView_NEW,
                                                                       Messages.AuthorizeIdView_SelGroup,
                                                                       Messages.AuthorizeIdView_SelRole,
                                                                       null,
                                                                       null);
                
                if (dialog.open() == Window.OK) {
                    final String _group = _categoryCombo.getText();
                    final String _eaig = dialog.getValue();
                    final String _eair = dialog.getValue2();
                    
                    final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;
                    
                    final AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
                    try {
                        nd.insertNewData(_name, _group, oclass2, _eair, _eaig);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog
                                .openError(getSite().getShell(),
                                           "LDAP error.",
                                           "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
                    
                    refreshGroupRoleTable();
                    
                }
            }
        });
        
        /**
         * "Edit" button for second table.
         */
        final Button _edit = new Button(parent, SWT.PUSH);
        _edit.setText(Messages.AuthorizeIdView_EDIT);
        _edit.setEnabled(canExecute);
        _edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String _name = _authorizeIdTable.getSelection()[0].getText();
                final String eaigSel = _groupRoleTable.getSelection()[0].getText(0);
                final String eairSel = _groupRoleTable.getSelection()[0].getText(1);
                final CustomInputDialog dialog = new CustomInputDialog(Display.getCurrent()
                                                                               .getActiveShell(),
                                                                       Messages.AuthorizeIdView_EDIT,
                                                                       Messages.AuthorizeIdView_GroupEdit,
                                                                       Messages.AuthorizeIdView_RoleEdit,
                                                                       eaigSel,
                                                                       eairSel);
                if (dialog.open() == Window.OK) {
                    final String _group = _categoryCombo.getText();
                    final String _eaig = dialog.getValue();
                    final String _eair = dialog.getValue2();
                    
                    final ObjectClass2 oclass2 = ObjectClass2.AUTHORIZEID;
                    
                    final AuthorizationIdGRManagement nd = new AuthorizationIdGRManagement();
                    try {
                        nd.deleteData(_name, eairSel, eaigSel, _group);
                        
                        nd.insertNewData(_name, _group, oclass2, _eair, _eaig);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog
                                .openError(getSite().getShell(),
                                           "LDAP error.",
                                           "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
                    refreshGroupRoleTable();
                    
                }
            }
        });
        
        /**
         * "Delete" button for second table.
         */
        final Button _delete = new Button(parent, SWT.PUSH);
        _delete.setText(Messages.AuthorizeIdView_DELETE);
        _delete.setEnabled(canExecute);
        _delete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final String _name = _authorizeIdTable.getSelection()[0].getText();
                final String _eaig = _groupRoleTable.getSelection()[0].getText();
                final String _eair = _groupRoleTable.getSelection()[0].getText(1);
                final String _group = _categoryCombo.getText();
                
                final MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
                                                             SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                messageBox.setMessage(Messages.AuthorizeIdView_DelWarn);
                messageBox.setText(Messages.AuthorizeIdView_DelEntry);
                final int response = messageBox.open();
                if (response == SWT.YES) {
                    final AuthorizationIdGRManagement aim = new AuthorizationIdGRManagement();
                    try {
                        aim.deleteData(_name, _eair, _eaig, _group);
                    } catch (final ServiceUnavailableException e1) {
                        MessageDialog
                                .openError(getSite().getShell(),
                                           "LDAP error.",
                                           "LDAP service unavailable, try again later or start the LDAP service manually.");
                        return;
                    } catch (final InvalidNameException e1) {
                        MessageDialog.openError(getSite().getShell(),
                                                "LDAP naming error.",
                                                "LDAP action failed.\n" + e1.getMessage());
                        return;
                    }
                    refreshGroupRoleTable();
                }
            }
        });
    }
    
    /**
     * Deletes all data and fills the authorize id table again.
     */
    private void refreshAuthorizeIdTable() {
        final String[] authorizeIds = LdapAccess.getEain(_categoryCombo.getText());
        _authorizeIdTableViewer.setInput(authorizeIds);
    }
    
    /**
     * Deletes all data and fills the group / role table again.
     */
    private void refreshGroupRoleTable() {
        final AuthorizeIdEntry[] entries = LdapAccess.getProp(_authorizeIdTable.getSelection()[0]
                .getText(), _categoryCombo.getText());
        
        _groupRoleTableViewer.setInput(entries);
    }
    
    /**
     * @param name
     * @param group
     */
    private void deleteWholeGroupRoleTable(final String name, final String group) {
        final AuthorizationIdGRManagement aim2 = new AuthorizationIdGRManagement();
        
        for (int i = 0; i < _groupRoleTable.getItemCount(); i++) {
            if (_groupRoleTable.getItem(i).getText(0).equals("")) { //$NON-NLS-1$
                break;
            }
            
            final String _eaig = _groupRoleTable.getItem(i).getText(0);
            final String _eair = _groupRoleTable.getItem(i).getText(1);
            try {
                aim2.deleteData(name, _eair, _eaig, group);
            } catch (final ServiceUnavailableException e1) {
                MessageDialog
                        .openError(getSite().getShell(),
                                   "LDAP error.",
                                   "LDAP service unavailable, try again later or start the LDAP service manually.");
                return;
            } catch (final InvalidNameException e1) {
                MessageDialog.openError(getSite().getShell(),
                                        "LDAP naming error.",
                                        "LDAP action failed.\n" + e1.getMessage());
                return;
            }
            
        }
    }
    
    @Override
    public void setFocus() {
        _categoryCombo.setFocus();
    }
    
    private static class AuthorizeIdTableFactory {
        
        /**
         * @param parent a composite
         */
        private static TableViewer createAuthorizeIdTableViewer(final Composite parent) {
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
    
    private static class GroupRoleTableFactory {
        
        private static Table _groupRoleTable;
        
        /**
         * @param parent a composite
         */
        private static TableViewer createGroupRoleTableViewer(final Composite parent) {
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
            
            groupRoleTableViewer.setContentProvider(new AuthorizeIdContentProvider());
            groupRoleTableViewer.setLabelProvider(new AuthorizeIdLabelProvider());

            return groupRoleTableViewer;
        }

        private static TableColumn createGroupColumn(@Nonnull final TableViewer viewer,
                                                     @Nonnull final TableColumnLayout tableColumnLayout) {
            final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
            final TableColumn column = tableViewerColumn.getColumn();
            column.setText(Messages.AuthorizeIdView_EAIG);
            tableColumnLayout
                    .setColumnData(column, new ColumnWeightData(15, ColumnWeightData.MINIMUM_WIDTH));
            column.addListener(SWT.Selection, new MyListener(0));
            return column;
        }

        private static TableColumn createRoleColumn(@Nonnull final TableViewer viewer,
                                                    @Nonnull final TableColumnLayout tableColumnLayout) {
            final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
            final TableColumn column = tableViewerColumn.getColumn();
            column.setText(Messages.AuthorizeIdView_EAIR);
            
            tableColumnLayout
                    .setColumnData(column, new ColumnWeightData(15, ColumnWeightData.MINIMUM_WIDTH));
            column.addListener(SWT.Selection, new MyListener(1));
            return column;
        }
        
        private static TableColumn createUsersColumn(@Nonnull final TableViewer viewer,
                                                    @Nonnull final TableColumnLayout tableColumnLayout) {
            final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.None);
            final TableColumn column = tableViewerColumn.getColumn();
            column.setText(Messages.AuthorizeIdView_USERS);
            
            tableColumnLayout
                    .setColumnData(column, new ColumnWeightData(70, ColumnWeightData.MINIMUM_WIDTH));
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
    
}
