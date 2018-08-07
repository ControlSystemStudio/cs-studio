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

    public ToggleToolbarAction(final RTPlot<?> plot, final boolean is_visible)
    {
        super(is_visible ? Messages.Toolbar_Hide : Messages.Toolbar_Show,
              Activator.getIcon("toolbar"));
        this.plot = plot;
    }

    public void updateText()
    {
        setText(plot.isToolbarVisible() ? Messages.Toolbar_Hide : Messages.Toolbar_Show);
    }

    @Override
    public void run()
    {
        plot.showToolbar(! plot.isToolbarVisible());
    }
}
