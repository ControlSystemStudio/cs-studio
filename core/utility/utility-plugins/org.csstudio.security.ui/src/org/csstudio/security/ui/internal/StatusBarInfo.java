/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/** Display user name in status bar.
 *
 *  @author Joerg Rathlev - Original LoginInformationToolbar
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls") // TODO Externalize strings
public class StatusBarInfo extends WorkbenchWindowControlContribution
   implements SecurityListener
{
    private Button user_name;

    @Override
    protected Control createControl(final Composite parent)
    {
        final Composite top = new Composite(parent, 0);
        top.setLayout(new FillLayout());
        user_name = new Button(top, SWT.FLAT);
        user_name.setText("Login info...");
        user_name.setToolTipText("Name of the current user");
        user_name.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                final IWorkbench workbench = PlatformUI.getWorkbench();
                final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                try
                {
                    final IWorkbenchPage page = window.getActivePage();
                    page.showView(SecurityInfoView.ID);
                }
                catch (Exception ex)
                {
                    Logger.getLogger(StatusBarInfo.class.getName()).log(Level.WARNING, "Cannot open view", ex);
                }
            }
        });

        // Trigger initial update
        changedSecurity(SecuritySupport.getSubject(),
                SecuritySupport.isCurrentUser(),
                SecuritySupport.getAuthorizations());

        // Subscribe to changes
        SecuritySupport.addListener(this);
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                SecuritySupport.removeListener(StatusBarInfo.this);
            }
        });

        return top;
    }

    @Override
    public void changedSecurity(final Subject subject,
            final boolean is_current_user, final Authorizations authorizations)
    {
        if (user_name.isDisposed())
            return;
        user_name.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (user_name.isDisposed())
                    return;
                if (subject == null)
                    user_name.setText("");
                else
                    user_name.setText(SecuritySupport.getSubjectName(subject));
            }
        });
    }
}
