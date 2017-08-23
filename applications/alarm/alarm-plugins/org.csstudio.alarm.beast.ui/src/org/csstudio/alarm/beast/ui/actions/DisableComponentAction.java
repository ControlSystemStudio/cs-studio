/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.security.ui.SecuritySupportUI;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that disables a PV or other Component and all of its children from the configuration.
 *  @author Kay Kasemir
 *  @author Xinyu Wu
 */
public class DisableComponentAction extends Action
{
    final private Shell shell;
    final private AlarmClientModel model;
    final private AlarmTreeItem items[];

    /** Initialize action
     *  @param shell Shell
     *  @param model Alarm model
     *  @param items Items to disable
     */
    public DisableComponentAction(final Shell shell, final AlarmClientModel model,
            final List<AlarmTreeItem> items)
    {
        setText(doEnable() ? Messages.EnableAlarms : Messages.DisableAlarms);
        setImageDescriptor(
                Activator.getImageDescriptor(
                    doEnable() ? "icons/enable_alarm.png" //$NON-NLS-1$
                               : "icons/disable_alarm.png")); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.items = new AlarmTreeItem[items.size()];
        items.toArray(this.items);

        // authorization
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);
    }

    protected boolean doEnable()
    {
        return false;
    }

    @Override
    public void run()
    {   // Locate PVs
        final List<AlarmTreePV> pvs = new ArrayList<>();
        for (AlarmTreeItem item : items)
            addPVs(pvs, item);
        if (pvs.size() > 1)
            if (!MessageDialog.openConfirm(shell, getText(),
                NLS.bind(doEnable()
                    ? Messages.EnableAlarmsFmt
                    : Messages.DisableAlarmsFmt,
                    pvs.size())))
                return;
        for (AlarmTreePV pv : pvs)
        {
            try
            {
                model.enable(pv, doEnable());
            }
            catch (Exception ex)
            {
                ExceptionDetailsErrorDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.EnablementErrorFmt, pv.getPathName()), ex);
            }
        }
    }

    /** @param pvs List where PVs to enable/disable will be added
     *  @param item Item for which to locate PVs, recursively
     */
    protected void addPVs(final List<AlarmTreePV> pvs, final AlarmTreeItem item)
    {
        if (item instanceof AlarmTreePV)
        {
            final AlarmTreePV pv = (AlarmTreePV) item;
            if (pv.isEnabled() != doEnable())
                pvs.add(pv);
        }
        else
        {
            final int N = item.getChildCount();
            for (int i=0; i<N; ++i)
                addPVs(pvs, item.getChild(i));
        }
    }
}
