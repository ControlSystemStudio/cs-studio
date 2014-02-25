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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/** Eclipse view that displays security info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls") // No externalize strings, not meant for end users
public class SecurityInfoView extends ViewPart implements SecurityListener
{
	// View ID
	final public static String ID = "org.csstudio.security.info";
    private Text user;
    private List subject_detail, authorization_detail;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        createComponents(parent);
        
        // Add demo actions
        final String[] demo_actions = new String[] { "alarm_config", "alarm_acknowledge" };
        final IAction[] actions = new IAction[demo_actions.length];
        for (int i=0; i<demo_actions.length; ++i)
        {
            final String authorization = demo_actions[i];
            actions[i] = new Action("Test " + (i+1))
            {
                @Override
                public void runWithEvent(Event event)
                {
                    MessageDialog.openInformation(getSite().getShell(), "Test", "You have the '" + authorization + "' authorization");
                }
            };
            SecuritySupportUI.registerAction(actions[i], authorization);
            getViewSite().getActionBars().getMenuManager().add(actions[i]);
            getViewSite().getActionBars().getToolBarManager().add(actions[i]);
        }
        
        // Toggle initial update
        changedSecurity(SecuritySupport.getSubject(),
                SecuritySupport.isCurrentUser(),
                SecuritySupport.getAuthorizations());

        // Update when security info changes
        SecuritySupport.addListener(this);
        
        // Unregister actions and listener
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                for (int i=0; i<demo_actions.length; ++i)
                {
                    SecuritySupportUI.unregisterAction(actions[i], demo_actions[i]);
                }
                SecuritySupport.removeListener(SecurityInfoView.this);
            }
        });
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createComponents(final Composite parent)
    {
        parent.setLayout(new FillLayout());
        
        final SashForm sashes = new SashForm(parent, SWT.VERTICAL);

        // Top
        final Composite top = new Composite(sashes, 0);
        top.setLayout(new GridLayout(2, false));

        Label l = new Label(top, 0);
        l.setText("User:");
        l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        user = new Text(top, SWT.BORDER | SWT.READ_ONLY);
        user.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        l = new Label(top, 0);
        l.setText("Logged in User:");
        l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        
        subject_detail = new List(top, SWT.V_SCROLL);
        subject_detail.setToolTipText("List of 'principals', names associated with the user");
        subject_detail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Bottom
        final Composite bottom = new Composite(sashes, 0);
        bottom.setLayout(new GridLayout(2, false));
        
        l = new Label(bottom, 0);
        l.setText("Authorizations:");
        l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        
        authorization_detail = new List(bottom, SWT.V_SCROLL);
        authorization_detail.setToolTipText("List of authorizations held by the user");
        authorization_detail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        sashes.setWeights(new int[] { 50, 50 });
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        user.setFocus();
    }

    /** {@inheritDoc} */
    @Override
    public void changedSecurity(final Subject subject,
            final boolean is_current_user, final Authorizations authorizations)
    {
        final Collection<String> user_info = new ArrayList<>();
        final String user_text;
        
        if (subject == null)
            user_text = "- Not logged in -";
        else
        {
            if (is_current_user)
                user_text = SecuritySupport.getSubjectName(subject) + " (current user)";
            else
                user_text = SecuritySupport.getSubjectName(subject);

            user_info.add("Principals:");
            for (Principal p : subject.getPrincipals())
                user_info.add(" " + p.toString());
            user_info.add("Public Credentials:");
            for (Object c : subject.getPublicCredentials())
                user_info.add(" " + c.toString());
            user_info.add("Private Credentials:");
            for (Object c : subject.getPrivateCredentials())
                user_info.add(" " + c.toString());
        }
        final Collection<String> auth_info = new ArrayList<>();
        if (authorizations != null)
            auth_info.addAll(authorizations.getAuthorizations());
        
        subject_detail.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                user.setText(user_text);
                subject_detail.setItems(user_info.toArray(new String[user_info.size()]));
                authorization_detail.setItems(auth_info.toArray(new String[auth_info.size()]));
            }
        });
    }
}
