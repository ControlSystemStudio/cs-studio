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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
    private Label user_name;

    @Override
    protected Control createControl(final Composite parent)
    {
        final Composite top = new Composite(parent, 0);
        top.setLayout(new FillLayout());
        user_name = new Label(top, 0);
        user_name.setText("Login info...");
        user_name.setToolTipText("Name of the current user");
        
        // Trigger initial update
        changedSecurity(SecuritySupport.getSubject(), SecuritySupport.getAuthorizations());
        
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
    public void changedSecurity(final Subject subject, final Authorizations authorizations)
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
                try
                {
                    if (subject == null)
                        user_name.setText("");
                    else
                        user_name.setText(SecuritySupport.getSubjectName(subject));
                }
                catch (Exception ex)
                {
                    Logger.getLogger(StatusBarInfo.class.getName())
                        .log(Level.WARNING, "Cannot update user name", ex);
                }
            }
        });
    }
}
