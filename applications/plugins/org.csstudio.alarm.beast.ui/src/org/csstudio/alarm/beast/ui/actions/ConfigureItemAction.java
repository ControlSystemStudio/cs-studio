/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that configures an alarm tree item
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class ConfigureItemAction extends AbstractUserDependentAction
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
                Activator.getImageDescriptor("icons/configure.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.item = item;

        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

    /** Initialize action
     *  @param selection_provider Selection provider that must give AlarmTree items
     */
    public ConfigureItemAction(final Shell shell, final AlarmClientModel model, final ISelectionProvider selection_provider)
    {
        super(Messages.ConfigureItem,
                Activator.getImageDescriptor("icons/configure.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.selection_provider = selection_provider;
        // Enable only when single item is selected
        selection_provider.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
            	boolean oneSelected=(((IStructuredSelection)event.getSelection()).size() == 1);
            	if(oneSelected) {
                	setEnabledWithoutAuthorization(true);
                	//authorization
                	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
                }else {
                	setEnabledWithoutAuthorization(false);
                	setEnabled(false);
                }
            }
        });
        setEnabled(false);
        setEnabledWithoutAuthorization(false);
    }


	@Override
	protected void doWork() {
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
        final ItemConfigDialog dlg = new ItemConfigDialog(shell, item);
        if (dlg.open() != Window.OK)
            return;
        try
        {
            if (item instanceof AlarmTreePV)
                model.configurePV((AlarmTreePV) item, dlg.getDescription(),
                        dlg.isEnabled(), dlg.isAnnunciate(), dlg.isLatch(),
                        dlg.getDelay(), dlg.getCount(),
                        dlg.getFilter(),
                        dlg.getGuidance(), dlg.getDisplays(), dlg.getCommands(), dlg.getAutomatedActions());
            else
                model.configureItem(item, dlg.getGuidance(), dlg.getDisplays(),
                        dlg.getCommands(), dlg.getAutomatedActions());

        }
        catch (Throwable ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.CannotUpdateConfigurationErrorFmt,
                            item.getName(), ex.getMessage()));
        }
	}
}
