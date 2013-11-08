/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.jface.action.Action;

/** Action that shows/hides the XYGraph's toolbar
 *  @author Kay Kasemir
 */
public class ToggleToolbarAction extends Action
{
    final private ToolbarArmedXYGraph plot;

    public ToggleToolbarAction(final ToolbarArmedXYGraph plot)
    {
        super(Messages.Toolbar_Show,
              Activator.getImageDescriptor("icons/toolbar.gif")); //$NON-NLS-1$
        this.plot = plot;
    }

    @Override
    public void run()
    {
        if (plot.isShowToolbar())
        {
            plot.setShowToolbar(false);
            setText(Messages.Toolbar_Show);
        }
        else
        {
            plot.setShowToolbar(true);
            setText(Messages.Toolbar_Hide);
        }
    }
}
