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
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

/** Action to control the "maintenance" mode
 *  @author Kay Kasemir
 */
public class MaintenanceModeAction extends Action implements AlarmClientModelListener
{
    /** Images for button icon */
    private static ImageDescriptor image_on = null, image_off = null;

    /** Model who's Mode we control */
    private AlarmClientModel model;

    /** Initialize
     *  @param model Model who's Mode we control
     */
    public MaintenanceModeAction(final AlarmClientModel model)
    {
        super(Messages.MaintenanceMode);
        getIcons();

        //authorization
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);

        setModel(model);
    }

    public void setModel(AlarmClientModel model)
    {
        if (this.model != null)
            this.model.removeListener(this);
        // Reflect mode of model right now, then monitor for mode changes
        this.model = model;
        if (this.model != null)
        {
            reflectModelMode(this.model.inMaintenanceMode());
            this.model.addListener(this);
        }
    }

    /** Assert that icons are loaded */
    private static void getIcons()
    {
        if (image_off != null)
            return;
        image_on = Activator.getImageDescriptor("icons/maintenance_act.gif"); //$NON-NLS-1$
        image_off = Activator.getImageDescriptor("icons/operate.gif"); //$NON-NLS-1$
    }

    /** Update button show show current model mode */
    private void reflectModelMode(final boolean maintenance_mode)
    {
        setImageDescriptor(maintenance_mode ? image_on : image_off);
        setToolTipText(maintenance_mode ? Messages.MaintenanceModeTT : Messages.NormalModeTT);
    }

    @Override
    public void run()
    {
        if (model.inMaintenanceMode())
        {
            if (!MessageDialog.openConfirm(null, Messages.MaintenanceMode,
                                           Messages.MaintenanceModeDisableMsg))
                return;
            model.requestMaintenanceMode(false);
        }
        else
        {
            if (!MessageDialog.openConfirm(null, Messages.MaintenanceMode,
                                           Messages.MaintenanceModeEnableMsg))
                return;
            model.requestMaintenanceMode(true);
        }
    }

    @Override
    public void serverModeUpdate(final AlarmClientModel model, final boolean maintenance_mode)
    {
        Display.getDefault().asyncExec(() -> reflectModelMode(maintenance_mode));
    }
    @Override
    public void serverTimeout(AlarmClientModel model) { /* Ignore */ }
    @Override
    public void newAlarmConfiguration(AlarmClientModel model) { /* Ignore */ }
    @Override
    public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed)
    { /* Ignore */ }
}
