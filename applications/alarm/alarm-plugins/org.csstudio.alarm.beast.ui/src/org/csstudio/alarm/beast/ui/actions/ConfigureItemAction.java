/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Shell;

/** Action that configures an alarm tree item
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class ConfigureItemAction extends Action
{
    private ISelectionProvider selection_provider;
    private Shell shell;
    private AlarmClientModel model;
    private AlarmTreeItem item;

    /** Initialize
     *  @param shell Shell
     *  @param model Model that contains the PV
     *  @param item PV to configure
     */
    public ConfigureItemAction(final Shell shell, final AlarmClientModel model,
            final AlarmTreeItem item)
    {
        super(Messages.ConfigureItem,
              Activator.getImageDescriptor("icons/configure.gif")); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.item = item;

        //authorization
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);
    }

    /** Initialize action
     *  @param selection_provider Selection provider that must give AlarmTree items
     */
    public ConfigureItemAction(final Shell shell, final AlarmClientModel model, final ISelectionProvider selection_provider)
    {
        super(Messages.ConfigureItem,
              Activator.getImageDescriptor("icons/configure.gif")); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.selection_provider = selection_provider;
        // Enable only when single item is selected
        selection_provider.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(final SelectionChangedEvent event)
            {
                final boolean oneSelected = (((IStructuredSelection)event.getSelection()).size() == 1);
                if (oneSelected)
                    //authorization
                    setEnabled(SecuritySupport.havePermission(AuthIDs.CONFIGURE));
                else
                    setEnabled(false);
            }
        });
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);
    }


    @Override
    public void run()
    {
        if (selection_provider != null)
            item =
                (AlarmTreeItem) ((IStructuredSelection)selection_provider.getSelection()).getFirstElement();
        // else: Fixed item passed into constructor
        performItemConfiguration(shell, model, item);
    }

    /** Interactively configure an item
     *
     *  <p>Can also be called from outside of the {@link ConfigureItemAction}
     *
     *  @param shell Parent shell for dialog
     *  @param model Model to configure
     *  @param item Item to configure
     */
    public static void performItemConfiguration(final Shell shell,
            final AlarmClientModel model, final AlarmTreeItem item)
    {
        final ItemConfigDialog dlg = new ItemConfigDialog(shell, item, model, false);
        dlg.open();
    }
}
