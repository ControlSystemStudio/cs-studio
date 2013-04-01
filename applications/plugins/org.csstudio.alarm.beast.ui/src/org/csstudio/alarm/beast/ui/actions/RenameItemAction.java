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
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that renames an alarm tree item
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class RenameItemAction extends Action
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
              Activator.getImageDescriptor("icons/rename.gif")); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.item = item;

        //authorization
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);
    }

    /** Open rename dialog
     *  @see org.eclipse.jface.action.Action#run()
     */
	public void run()
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
