/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.eclipse.jface.action.Action;

/** Action that shows/hides the XYGraph's toolbar
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ToggleToolbarAction extends Action
{
    final private RTPlot<?> plot;

    public ToggleToolbarAction(final RTPlot<?> plot)
    {
        super(Messages.Toolbar_Show, Action.AS_CHECK_BOX);
        this.setImageDescriptor(Activator.getIcon("toolbar"));
        this.setChecked(plot.isToolbarVisible());
        this.plot = plot;
    }

    public void update()
    {
        setChecked(plot.isToolbarVisible());
    }

    @Override
    public void run()
    {
        plot.showToolbar(! plot.isToolbarVisible());
    }

}