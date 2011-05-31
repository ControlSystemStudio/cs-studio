/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that removes a Component or PV from the configuration.
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class RemoveComponentAction extends AbstractUserDependentAction
{
    final private Shell shell;
    final private AlarmClientModel model;
    final private AlarmTreeItem items[];

    /** Initialize action
     *  @param shell Shell
     *  @param model Alarm model
     *  @param items Items to remove
     */
    public RemoveComponentAction(final Shell shell, final AlarmClientModel model,
            final List<AlarmTreeItem> items)
    {
        super(Messages.RemoveComponents,
              Activator.getImageDescriptor("icons/delete.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.items = new AlarmTreeItem[items.size()];
        items.toArray(this.items);

        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

    /** Prompt for PV name, add it to model
     *  @see org.eclipse.jface.action.Action#run()
     */
	@Override
	protected void doWork() {
		final StringBuilder names = new StringBuilder();
        for (AlarmTreeItem item : items)
        {
            if (names.length() > 0)
                names.append(", "); //$NON-NLS-1$
            names.append(item.getName());
        }
        if (!MessageDialog.openConfirm(shell, Messages.RemoveComponents,
                NLS.bind(Messages.RemoveConfirmationFmt,
                        names.toString())))
            return;
        for (AlarmTreeItem item : items)
        {
            try
            {
                model.remove(item);
            }
            catch (Exception ex)
            {
                MessageDialog.openError(shell, Messages.Error,
                     NLS.bind(Messages.RemovalErrorFmt,
                              item.getName(), ex.getMessage()));
            }
        }
	}
}
