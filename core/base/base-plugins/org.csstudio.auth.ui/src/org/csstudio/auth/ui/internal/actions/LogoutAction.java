/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.auth.ui.internal.actions;

import org.csstudio.auth.internal.usermanagement.IUserManagementListener;
import org.csstudio.auth.internal.usermanagement.UserManagementEvent;
import org.csstudio.auth.security.Credentials;
import org.csstudio.auth.security.ILoginCallbackHandler;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.Messages;
import org.csstudio.auth.ui.internal.AuthUiActivator;
import org.csstudio.auth.ui.security.UiLoginCallbackHandler;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Dynamic handler for "Log out" menu or toolbar item.
 *
 *  <p>Logged in user can log out via this action.
 *  Same as switching to anonymous user.
 *
 *  <p>Dynamic menu contribution was used because that
 *  seemed the easiest way to enable/disable the menu entry
 *  based on the current log in state of the user.
 *
 *  @author Xihui Chen - Original Action
 *  @author Kay Kasemir - Dynamic ContributionItem
 */
public class LogoutAction extends ContributionItem implements IUserManagementListener
{
    private boolean enabled;
    private Image icon;
    private ToolItem toolitem;

    /** Initialize */
    public LogoutAction()
    {
        final SecurityFacade security = SecurityFacade.getInstance();
        enabled = security.getCurrentUser() != null;
        security.addUserManagementListener(this);

        icon = AbstractUIPlugin.imageDescriptorFromPlugin(
                 AuthUiActivator.PLUGIN_ID, "icons/logout.png").createImage(); //$NON-NLS-1$
    }

    @Override
    public void dispose()
    {
        icon.dispose();

        final SecurityFacade security = SecurityFacade.getInstance();
        security.removeUserManagementListener(this);

        super.dispose();
    }

    @Override
    public void fill(final ToolBar toolbar, final int index)
    {
        toolitem = new ToolItem(toolbar, SWT.PUSH, index);
        toolitem.setEnabled(enabled);
        toolitem.setImage(icon);

        toolitem.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                log_out();
            }
        });
    }

    @Override
    public void fill(final Menu menu, final int index)
    {
        final MenuItem menuitem = new MenuItem(menu, SWT.PUSH, index);
        menuitem.setText(Messages.LogOut);
        menuitem.setEnabled(enabled);
        menuitem.setImage(icon);

        menuitem.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                log_out();
            }
        });
    }

    /** Perform log out */
    protected void log_out()
    {
        // Logout is same as  as anonymous login
        final SecurityFacade sf = SecurityFacade.getInstance();
        final ILoginCallbackHandler oldLCH = sf.getRegisteredLoginCallbackHandler();
        sf.setLoginCallbackHandler(new UiLoginCallbackHandler("","", Credentials.ANONYMOUS)); //$NON-NLS-1$ //$NON-NLS-2$
        sf.authenticateApplicationUser();
        sf.setLoginCallbackHandler(oldLCH);
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    /** @see IUserManagementListener */
    @Override
    public void handleUserManagementEvent(final UserManagementEvent event)
    {
        enabled = SecurityFacade.getInstance().getCurrentUser() != null;

        // The menu item is re-created each time the menu opens up.
        // The tool item stays on the tool bar, so update it:
        if (toolitem != null)
            toolitem.setEnabled(enabled);
    }
}
