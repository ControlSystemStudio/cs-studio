/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import org.csstudio.alarm.beast.annunciator.Activator;
import org.csstudio.alarm.beast.annunciator.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Action to silence annunciations
 *  @author Kay Kasemir
 */
public class SilenceAction extends Action
{
    final private static String ICON_SILENCE = "icons/silence.png"; //$NON-NLS-1$
    final private static String ICON_ACTIVE = "icons/annunciator.png"; //$NON-NLS-1$
    final private AnnunciatorView view;

    public SilenceAction(final AnnunciatorView view)
    {
        super(Messages.Silence, AS_CHECK_BOX);
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ICON_ACTIVE));
        setToolTipText(Messages.SilenceTT);

        this.view = view;
    }

    @Override
    public void run()
    {
        final boolean enable = ! isChecked();
        view.setAnnunciationsEnabled(enable);

        if (enable)
            setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ICON_ACTIVE));
        else
            setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, ICON_SILENCE));
    }
}
