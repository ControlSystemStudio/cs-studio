/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTTimePlot;

/** Enable/disable scrolling
 *  @author Kay Kasemir
 */
public class UpdateScrolling extends UndoableAction
{
    final private RTTimePlot plot;
    final private boolean enable;

    public UpdateScrolling(final RTTimePlot plot, final boolean enable)
    {
        super(Messages.Scroll_OnOff);
        this.plot = plot;
        this.enable = enable;
    }

    @Override
    public void run()
    {
        plot.setScrolling(enable);
    }

    @Override
    public void undo()
    {
        plot.setScrolling(!enable);
    }
}
