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
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that renames an alarm tree item
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class RenameItemAction extends AbstractUserDependentAction
{
    private Shell shell;
    private AlarmClientModel model;
    private AlarmTreeItem item;

    /** Initialize
     *  @param shell Shell
     *  @param model Model that contains the PV
     *  @param item PV to configure
     */
    public RenameItemAction(final Shell shell, final AlarmClientModel model,
            final AlarmTreeItem item)
    {
        super(Messages.RenameItem,
                Activator.getImageDescriptor("icons/rename.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.item = item;

        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

    /** Open rename dialog
     *  @see org.eclipse.jface.action.Action#run()
     */
	@Override
	protected void doWork()
	{
		final InputDialog dlg = new InputDialog(shell, Messages.RenameItem,
                Messages.RenameItemMsg, item.getName(), null);
        if (dlg.open() != Window.OK)
            return;
        try
        {
            model.rename(item, dlg.getValue());
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.CannotUpdateConfigurationErrorFmt,
                            item.getName(), ex.getMessage()));
        }
	}
}
