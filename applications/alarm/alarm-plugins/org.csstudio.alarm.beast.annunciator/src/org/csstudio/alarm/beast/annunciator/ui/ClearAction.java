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

/** Action to clear list of annunciations
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ClearAction extends Action
{
    final private AnnunciatorView view;

    public ClearAction(final AnnunciatorView view)
    {
        super(Messages.Clear,
            AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/removeall.gif"));
        setToolTipText(Messages.ClearTT);

        this.view = view;
    }

    @Override
    public void run()
    {
        view.clearAnnunciations();
    }
}
