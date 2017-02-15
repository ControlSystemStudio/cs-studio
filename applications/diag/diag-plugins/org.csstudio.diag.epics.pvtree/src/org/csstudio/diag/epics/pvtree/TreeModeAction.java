/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.diag.epics.pvtree.model.TreeModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Action to change tree mode
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TreeModeAction extends Action
{
    private static ImageDescriptor run, pause_on_alarm;
    final private TreeModel model;

    public TreeModeAction(final TreeModel model)
    {
        super(Messages.TreeMode);
        setToolTipText(Messages.TreeMode_TT);

        this.model = model;
        getIcons();
        reflectModelMode();
    }

    /** Assert that icons are loaded */
    private static void getIcons()
    {
        if (run != null)
            return;
        run = AbstractUIPlugin.imageDescriptorFromPlugin(Plugin.ID, "icons/run.png");
        pause_on_alarm = AbstractUIPlugin.imageDescriptorFromPlugin(Plugin.ID, "icons/pause_on_alarm.png");
    }

    /** Update icon to reflect current mode of model */
    private void reflectModelMode()
    {
        if (model.isLatchingOnAlarm())
            setImageDescriptor(pause_on_alarm);
        else
            setImageDescriptor(run);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.latchOnAlarm(! model.isLatchingOnAlarm());
        reflectModelMode();
    }
}
