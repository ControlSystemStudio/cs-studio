/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.ui.internal;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;

/** Eclipse view that displays security info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls") // TODO Externalize strings
public class SecurityInfoView extends ViewPart implements SecurityListener
{
    private List subject_detail, authorization_detail;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        createComponents(parent);
        
        // Add demo actions
        final IAction configure = new Action("Test Config")
        {
            @Override
            public void runWithEvent(Event event)
            {
                MessageDialog.openInformation(getSite().getShell(), "Test", "You have the 'alarm_config' authorization");
            }
        };
        SecuritySupportUI.registerAction(configure, "alarm_config");

        final IAction acknowledge = new Action("Test Ack'")
        {
            @Override
            public void runWithEvent(Event event)
            {
                MessageDialog.openInformation(getSite().getShell(), "Test", "You have the 'alarm_acknowledge' authorization");
            }
        };
        SecuritySupportUI.registerAction(acknowledge, "alarm_acknowledge");
        
        getViewSite().getActionBars().getMenuManager().add(configure);
        getViewSite().getActionBars().getMenuManager().add(acknowledge);
        getViewSite().getActionBars().getToolBarManager().add(configure);
        getViewSite().getActionBars().getToolBarManager().add(acknowledge);
        
        // Toggle initial update
        changedSecurity(SecuritySupport.getSubject(), SecuritySupport.getAuthorizations());

        // Update when security info changes
        SecuritySupport.addListener(this);
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                SecuritySupportUI.unregisterAction(configure, "alarm_config");
                SecuritySupportUI.unregisterAction(acknowledge, "alarm_acknowledge");
                SecuritySupport.removeListener(SecurityInfoView.this);
            }
        });
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createComponents(final Composite parent)
    {
        final GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);
        
        Label l = new Label(parent, 0);
        l.setText("Logged in User:");
        l.setLayoutData(new GridData(SWT.TOP, SWT.TOP, false, false));
        
        subject_detail = new List(parent, 0);
        subject_detail.setToolTipText("List of 'principals', names associated with the user");
        subject_detail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        l = new Label(parent, 0);
        l.setText("Authorizations:");
        l.setLayoutData(new GridData(SWT.TOP, SWT.TOP, false, false));
        
        authorization_detail = new List(parent, 0);
        authorization_detail.setToolTipText("List of authorizations held by the user");
        authorization_detail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    public void changedSecurity(final Subject subject, final Authorizations authorizations)
    {
        final Collection<String> user_info = new ArrayList<>();
        if (subject == null)
            user_info.add("- Not logged in -");
        else
        {
            for (Principal p : subject.getPrincipals())
                user_info.add(p.toString());
        }
        final Collection<String> auth_info = new ArrayList<>();
        if (authorizations != null)
            auth_info.addAll(authorizations.getAuthorizations());
        
        subject_detail.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                subject_detail.setItems(user_info.toArray(new String[user_info.size()]));
                authorization_detail.setItems(auth_info.toArray(new String[auth_info.size()]));
            }
        });
    }
}
