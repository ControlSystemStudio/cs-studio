/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.eclipse.jface.action.Action;

/** Action that shows/hides the XYGraph's toolbar
 *  @author Kay Kasemir
 */
public class ToggleToolbarAction extends Action
{
    final private Plot plot;

    public ToggleToolbarAction(final Plot plot)
    {
        super(Messages.Toolbar_Hide,
              Activator.getDefault().getImageDescriptor("icons/toolbar.gif")); //$NON-NLS-1$
        this.plot = plot;
    }

    @Override
    public void run()
    {
        if (plot.isToolbarVisible())
        {
            plot.setToolbarVisible(false);
            setText(Messages.Toolbar_Show);
        }
        else
        {
            plot.setToolbarVisible(true);
            setText(Messages.Toolbar_Hide);
        }
    }
}
