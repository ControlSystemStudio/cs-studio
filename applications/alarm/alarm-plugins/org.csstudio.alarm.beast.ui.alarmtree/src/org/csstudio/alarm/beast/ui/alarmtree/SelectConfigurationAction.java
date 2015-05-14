/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelConfigListener;
import org.csstudio.apputil.ui.swt.DropdownToolbarAction;
import org.eclipse.swt.widgets.Composite;

/** (Toolbar) action that shows the currently selected alarm configuration name
 *  and allows selection of a different alarm configuration
 *  @author Kay Kasemir
 */
public class SelectConfigurationAction extends DropdownToolbarAction implements AlarmClientModelConfigListener
{
    final private Composite parent;
    final private AlarmClientModel model;

    public SelectConfigurationAction(final Composite parent, final AlarmClientModel model)
    {
        super(model.getConfigurationName(), Messages.SelectAlarmConfiguration);
        this.parent = parent;
        this.model = model;
        setSelection(model.getConfigurationName());
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        return model.getConfigurationNames();
    }

    /** {@inheritDoc} */
    @Override
    public void handleSelection(final String option)
    {
        // Use item text to set model name
        try
        {
            if (model.setConfigurationName(option, SelectConfigurationAction.this))
                // Prohibit more changes while loading new config
                setEnabled(false);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Activator.ID).log(Level.SEVERE, "Cannot change alarm model", ex); //$NON-NLS-1$
        }
    }

    /** @see AlarmClientModelConfigListener */
    @Override
    public void newAlarmConfiguration(final AlarmClientModel model)
    {
        if (parent.isDisposed())
            return;
        parent.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (parent.isDisposed())
                    return;
                setText(model.getConfigurationName());
                setEnabled(true);
                // Since the toolbar item's text changed,
                // a re-layout of the toolbar could be required.
                // Tried all these to no avail: The item resizes
                // and might push other toolbar items out of the
                // window, but the toolbar does not properly re-layout.
//                toolbar.changed(toolbar.getChildren());
//                toolbar.pack();
//                toolbar.getParent().pack();
//                toolbar.getParent().getParent().pack();
//                toolbar.getParent().changed(new Control[] { toolbar });
            }
        });
    }
}
