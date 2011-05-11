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

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.naming.ServiceUnavailableException;

import org.apache.log4j.Logger;
import org.csstudio.auth.security.RegisteredAuthorizationId;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdGRManagement;
import org.csstudio.config.authorizeid.ldap.AuthorizationIdManagement;
import org.csstudio.config.authorizeid.ldap.LdapAccess;
import org.csstudio.config.authorizeid.ldap.ObjectClass1;
import org.csstudio.config.authorizeid.ldap.ObjectClass2;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/**
 * Tool to display and edit ldap content of authorization ID data
 * @author Rok Povsic / Jörg Penning
 */
public class AuthorizeIdView extends ViewPart {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AuthorizeIdView.class);
    
    public static final String ID = "org.csstudio.config.authorizeid";//$NON-NLS-1$
    
    private Combo _categoryCombo;
    
    private TableViewer _authorizeIdTableViewer;
    private TableViewer _groupRoleTableViewer;
    private TableViewer _registeredAuthorizeIdTableViewer;
    
    private static final String SECURITY_ID = "AuthorizeId";
    
    /**
     * Creates a view for the plugin.
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, true));
        
        final Group authIdGroupPanel = new Group(parent, SWT.NONE);
        authIdGroupPanel.setText(Messages.AuthorizeIdView_AUTH_IDS_FROM_LDAP);
        authIdGroupPanel.setLayout(new GridLayout(1, true));
        authIdGroupPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite authIDPanel = new Composite(authIdGroupPanel, SWT.FILL);
        authIDPanel.setLayout(new GridLayout(3, false));
        authIDPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Composite groupRolePanel = new Composite(authIdGroupPanel, SWT.FILL);
        groupRolePanel.setLayout(new GridLayout(2, false));
        groupRolePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Group registeredAuthIdGroupPanel = new Group(parent, SWT.NONE);
        registeredAuthIdGroupPanel.setText(Messages.AuthorizeIdView_AUTH_IDS_REGISTERED);
        registeredAuthIdGroupPanel.setLayout(new GridLayout(1, false));
        registeredAuthIdGroupPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createAuthIdPanelContent(authIDPanel);
        createGroupRolePanelContent(groupRolePanel);
        createRegisteredAuthIdPanelContent(registeredAuthIdGroupPanel);
        
        showRegisteredAuthIds();
    }
    
    private void showRegisteredAuthIds() {
        Collection<RegisteredAuthorizationId> authIds = SecurityFacade.getInstance()
                .getRegisteredAuthorizationIds();
        _registeredAuthorizeIdTableViewer.setInput(authIds.toArray());
    }
    
    private void createAuthIdPanelContent(@Nonnull final Composite authIDPanel) {
        final Group categoryGroupPanel = new Group(authIDPanel, SWT.NONE);
        categoryGroupPanel.setText(Messages.AuthorizeIdView_SELECT_GROUP);
        categoryGroupPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
        categoryGroupPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        
        Label categoryLabel = new Label(categoryGroupPanel, SWT.CENTER);
        categoryLabel.setText(Messages.AuthorizeIdView_GROUP);
        
        _categoryCombo = new Combo(categoryGroupPanel, SWT.NONE | SWT.READ_ONLY);
        
        String[] groups = new String[] { Messages.AuthorizeIdView_MessageWrong1 };
        
        try {
            groups = LdapAccess.getGroups();
        } catch (final Exception e) {
            LOG.warn("No groups found in LDAP", e);
        }
        
        for (final String group : groups) {
            _categoryCombo.add(group);
        }
        
        _categoryCombo.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(final SelectionEvent e) {
                refreshAuthorizeIdTable();
                getGroupRoleTable().removeAll();
                getGroupRoleTable().clearAll();
            }
            
        });
        
        final Composite authIdTablePanel = new Composite(authIDPanel, SWT.NONE);
        authIdTablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _authorizeIdTableViewer = AuthorizeIdTableViewerFactory.INSTANCE
                .createTableViewer(authIdTablePanel);
        getAuthorizeIdTable().addSelectionListener(new SelectionAdapter() {
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
    
    private void createGroupRolePanelContent(@Nonnull final Composite groupRolePanel) {
        final Composite groupRoleTablePanel = new Composite(groupRolePanel, SWT.NONE);
        groupRoleTablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _groupRoleTableViewer = GroupRoleTableViewerFactory.INSTANCE
                .createTableViewer(groupRoleTablePanel);
        getGroupRoleTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final Composite groupRoleButtonPanel = new Composite(groupRolePanel, SWT.NONE);
        groupRoleButtonPanel.setLayout(new FillLayout(SWT.VERTICAL));
        groupRoleButtonPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        createButtons2(groupRoleButtonPanel);
    }
    
    private void createRegisteredAuthIdPanelContent(@Nonnull final Composite registeredAuthIdPanel) {
        final Composite tablePanel = new Composite(registeredAuthIdPanel, SWT.NONE);
        tablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _registeredAuthorizeIdTableViewer = RegisteredAuthorizationIdTableViewerFactory.INSTANCE
                .createTableViewer(tablePanel);
        getRegisteredAuthorizeIdTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }
    
    /**
     * Creates first set of buttons.
     * @param parent a composite
     */
    private void createButtons1(final Composite parent) {
        /**
         * "New" button for the first table.
         */
        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, true);
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
                final String name = getAuthorizeIdTable().getSelection()[0].getText();
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
                        for (int i = 0; i < getGroupRoleTable().getItemCount(); i++) {
                            eaig.add(getGroupRoleTable().getItem(i).getText(0));
                            eair.add(getGroupRoleTable().getItem(i).getText(1));
                            
                            ndGr.deleteData(name, eair.get(i), eaig.get(i), _group);
                        }
                        
                        final ObjectClass1 oclass = ObjectClass1.AUTHORIZEID;
                        
                        final AuthorizationIdManagement nd = new AuthorizationIdManagement();
                        nd.deleteData(name, _group);
                        nd.insertNewData(_name, _group, oclass);
                        
                        for (int i = 0; i < getGroupRoleTable().getItemCount(); i++) {
                            
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
                
                final String _name = getAuthorizeIdTable().getSelection()[0].getText();
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
                final String _name = getAuthorizeIdTable().getSelection()[0].getText();
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
                final String _name = getAuthorizeIdTable().getSelection()[0].getText();
                final String eaigSel = getGroupRoleTable().getSelection()[0].getText(0);
                final String eairSel = getGroupRoleTable().getSelection()[0].getText(1);
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
                final String _name = getAuthorizeIdTable().getSelection()[0].getText();
                final String _eaig = getGroupRoleTable().getSelection()[0].getText();
                final String _eair = getGroupRoleTable().getSelection()[0].getText(1);
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

        final AuthorizedIdTableEntry[] entries = new AuthorizedIdTableEntry[authorizeIds.length];
        int ix = 0;
        for (String string : authorizeIds) {
            entries[ix] = AuthorizedIdTableEntry.createEntry(string);
            ix++;
        }
        
        _authorizeIdTableViewer.setInput(entries);
    }
    
    /**
     * Deletes all data and fills the group / role table again.
     */
    private void refreshGroupRoleTable() {
        final GroupRoleTableEntry[] entries = LdapAccess.getProp(getAuthorizeIdTable()
                .getSelection()[0].getText(), _categoryCombo.getText());
        
        _groupRoleTableViewer.setInput(entries);
    }
    
    /**
     * @param name
     * @param group
     */
    private void deleteWholeGroupRoleTable(final String name, final String group) {
        final AuthorizationIdGRManagement aim2 = new AuthorizationIdGRManagement();
        
        for (int i = 0; i < getGroupRoleTable().getItemCount(); i++) {
            if (getGroupRoleTable().getItem(i).getText(0).equals("")) { //$NON-NLS-1$
                break;
            }
            
            final String _eaig = getGroupRoleTable().getItem(i).getText(0);
            final String _eair = getGroupRoleTable().getItem(i).getText(1);
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
    
    @Nonnull
    public Table getAuthorizeIdTable() {
        return _authorizeIdTableViewer.getTable();
    }
    
    @Nonnull
    public Table getGroupRoleTable() {
        return _groupRoleTableViewer.getTable();
    }
    
    @Nonnull
    public Table getRegisteredAuthorizeIdTable() {
        return _registeredAuthorizeIdTableViewer.getTable();
    }
    
    @Override
    public void setFocus() {
        _categoryCombo.setFocus();
    }
    
}
