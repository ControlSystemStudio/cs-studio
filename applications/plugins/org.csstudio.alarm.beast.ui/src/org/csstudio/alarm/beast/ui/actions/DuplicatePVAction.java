/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreePV;
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

/** Action that duplicates an alarm tree PV
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class DuplicatePVAction extends AbstractUserDependentAction
{
    final private Shell shell;
    final private AlarmClientModel model;
    final private AlarmTreePV pv;

    /** Initialize
     *  @param shell Shell
     *  @param model Model that contains the PV
     *  @param pv PV to configure
     */
    public DuplicatePVAction(final Shell shell, final AlarmClientModel model,
            final AlarmTreePV pv)
    {
        super(Messages.DuplicatePV,
                Activator.getImageDescriptor("icons/move.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.pv = pv;

        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

	@Override
	protected void doWork()
	{
        final String path = pv.getPathName();
        final InputDialog dlg = new InputDialog(shell, Messages.DuplicatePV,
                NLS.bind(Messages.DuplicatePVMesgFmt,
                         pv.getName(), pv.getDescription()),
                path, null);
        if (dlg.open() != Window.OK)
            return;
        final String new_path = dlg.getValue();
        try
        {
            model.duplicatePV(pv, new_path);
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.CannotUpdateConfigurationErrorFmt,
                            new_path, ex.getMessage()));
        }
	}
}
