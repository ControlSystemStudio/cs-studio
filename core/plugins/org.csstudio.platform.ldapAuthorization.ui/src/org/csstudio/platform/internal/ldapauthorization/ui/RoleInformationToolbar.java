/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.platform.internal.ldapauthorization.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.csstudio.auth.security.IRight;
import org.csstudio.auth.security.Right;
import org.csstudio.auth.security.RightSet;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.platform.internal.ldapauthorization.LdapAuthorizationReader;
import org.csstudio.platform.internal.ldapauthorization.ui.localization.Messages;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 16.06.2010
 */
public class RoleInformationToolbar extends WorkbenchWindowControlContribution {

    /**
     * Listens for user management events and processes them by updating the
     * text in the status bar.
     */
    private class UserManagementListener implements IUserManagementListener {

        /**
         * {@inheritDoc}
         */
        public void handleUserManagementEvent(final UserManagementEvent event) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    // Force an update of the contribution manager. This will
                    // cause the contribution manager to ask the contribution
                    // (i.e., the login information toolbar) to re-create its
                    // control with the updated information.
                    final IContributionManager parent = RoleInformationToolbar.this.getParent();
                    // Parent can be null for SNS CSS
                    if (parent != null) {
                        parent.update(true);
                    }
                }
            });
        }
    }

    /**
     * Listens for the user management events.
     */
    private IUserManagementListener _listener;

    /**
     * Creates a new toolbar.
     */
    public RoleInformationToolbar() {
        // do nothing
    }

    /**
     * Creates a new toolbar with an id.
     *
     * @param id
     *            the id.
     */
    public RoleInformationToolbar(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createControl(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
		User user = SecurityFacade.getInstance().getCurrentUser();
		if (user != null) {
			List<String> roles = getRoles(user);
			final StringBuilder sb = new StringBuilder();
			sb.append(Messages.RoleInformationToolbar_Teaser);
			for (String users : roles) {
				sb.append(users);
				sb.append("\r\n"); //$NON-NLS-1$
			}

			Button button = new Button(composite, SWT.PUSH);
			button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
					false));
			button.setText(Messages.RoleInformationToolbar_ButtonText);
			button.setToolTipText(sb.toString());
			button.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					MessageDialog.openInformation(null, Messages.RoleInformationToolbar_Head, sb.toString());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					MessageDialog.openInformation(null, Messages.RoleInformationToolbar_Head, sb.toString());
				}
			});
        	
        }
        createListener();

        return composite;
    }

    /**
     * Returns the name of the currently logged in user.
     *
     * @return the name of the currently logged in user.
     */
    private List<String> getRoles(final User user) {
        LdapAuthorizationReader reader = new LdapAuthorizationReader();
        ArrayList<String> rigthNames = new ArrayList<String>();
        RightSet rights = reader.getRights(user);
        if (rights != null) {
            List<IRight> rights2 = rights.getRights();
            for (IRight iRight : rights2) {
                if (iRight instanceof Right) {
                    Right right = (Right) iRight;
                    rigthNames.add(right.getGroup() + ":" + right.getRole()); //$NON-NLS-1$
                }

            }
        }
        return rigthNames;
    }

    /**
     * Returns <code>true</code>.
     *
     * @return <code>true</code>.
     */
    @Override
    public boolean isDynamic() {
        // The login information toolbar must be marked as dynamic so that its
        // contribution manager will recreate the control (by calling the
        // createControl method) whenever it updates.
        return true;
    }

    /**
     * Creates the user management listener.
     */
    private void createListener() {
        // if there is still an old listener, close the old one first
        if (_listener != null) {
            closeListener();
        }

        _listener = new UserManagementListener();
        SecurityFacade.getInstance().addUserManagementListener(_listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        closeListener();
    }

    /**
     * Removes the user management listener from the security system.
     */
    private void closeListener() {
        SecurityFacade.getInstance().removeUserManagementListener(_listener);
    }

}
