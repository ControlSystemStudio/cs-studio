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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that moves an alarm tree item
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class MoveItemAction extends AbstractUserDependentAction
{
    private Shell shell;
    private AlarmClientModel model;
    private List<AlarmTreeItem> items;

    /** Initialize
     *  @param shell Shell
     *  @param model Model that contains the PV
     *  @param items PV to configure
     */
    public MoveItemAction(final Shell shell, final AlarmClientModel model,
            final List<AlarmTreeItem> items)
    {
        super(Messages.MoveItem,
                Activator.getImageDescriptor("icons/move.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.items = items;

        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

	@Override
	protected void doWork() {
		if (items.size() <= 0)
            return;

        if (!MessageDialog.openConfirm(shell, Messages.MoveItem,
                NLS.bind(Messages.MoveConfirmationFmt,
                         items.size())))
            return;


        final String path = items.get(0).getParent().getPathName();
        final InputDialog dlg = new InputDialog(shell, Messages.MoveItem,
                Messages.MoveItemMsg, path, null);
        if (dlg.open() != Window.OK)
            return;
        for (AlarmTreeItem item : items)
        {
            try
            {
                model.move(item, dlg.getValue());
            }
            catch (Throwable ex)
            {
                MessageDialog.openError(shell, Messages.Error,
                        NLS.bind(Messages.CannotUpdateConfigurationErrorFmt,
                                item.getName(), ex.getMessage()));
                break;
            }
        }
	}
}
