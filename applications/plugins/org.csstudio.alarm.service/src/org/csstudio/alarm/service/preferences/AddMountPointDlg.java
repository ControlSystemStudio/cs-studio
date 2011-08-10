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
 package org.csstudio.alarm.service.preferences;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;

import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * used for preferences
 * TODO: move into preferences package?
 * FIXME: this is still from the first alarm tree (by Jurij Kodre?) and should probably be rewritten
 *
 */
public class AddMountPointDlg extends Dialog {

    private List _mountPoints;
    private String[] _result = new String[0];

    public AddMountPointDlg(@Nonnull final Shell parentShell) {
        super(parentShell);
    }


    @Nonnull
    private String[] getSubDirs() {
        final java.util.List<String> strcoll = new ArrayList<String>();

        final ILdapService service = AlarmServiceActivator.getDefault().getLdapService();
        if (service == null) {
            MessageDialog.openError(getParentShell(), "LDAP Access", "LDAP service unavailable. Retry later.");
            return _result;
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                      any(ATTR_FIELD_OBJECT_CLASS),
                                                      SearchControls.ONELEVEL_SCOPE);

        if (result == null || result.getAnswerSet().isEmpty())  {
            MessageDialog.openInformation(getParentShell(), "LDAP subdir retrieval", "No subdirs found in LDAP.");
            return _result;
        }
        for (final SearchResult row : result.getAnswerSet()) {
            final String rname = row.getName();
            strcoll.add(LdapNameUtils.simpleName(rname));
        }
        return strcoll.toArray(new String[0]);
    }

    @Override
    protected void configureShell(@Nonnull final Shell newShell)    {
        super.configureShell(newShell);
        newShell.setText("Add new mount point");
    }

    @Override
    @Nonnull
    protected Control createDialogArea(@CheckForNull final Composite parent) {
        final Composite composite = (Composite)super.createDialogArea(parent);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        new Label(composite, 0).setText("Select mount point to add:");

        _mountPoints = new List(composite, SWT.SINGLE | SWT.V_SCROLL);
        _mountPoints.setItems(getSubDirs());
        _mountPoints.setLayoutData(new GridData(256));

        return composite;
    }

    private void save()    {
        _result = _mountPoints.getSelection();
    }

    @Nonnull
    public String getResult() {
        if (_result[0] == null) {
            return "";
        }
        return _result[0];
    }

    @Override
    protected void okPressed() {
        save();
        setReturnCode(0);
        super.close();
    }
}
