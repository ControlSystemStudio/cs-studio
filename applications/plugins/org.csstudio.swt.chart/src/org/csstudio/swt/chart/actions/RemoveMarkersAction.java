/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.actions;

import java.util.logging.Level;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** An action that removes all markers from a chart.
 *  @author Kay Kasemir
 */
public class RemoveMarkersAction extends Action
{
    private Chart chart;

    /** Constructor */
    public RemoveMarkersAction(Chart chart)
    {
        this.chart = chart;
        setText(Messages.RemoveMarkers);
        setToolTipText(Messages.RemoveMarkers_TT);
        // This will fail in unit tests w/o workbench
        try
        {
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING, "RemoveMarkersAction cannot get 'delete' icon", ex); //$NON-NLS-1$
        }
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            chart.getYAxis(i).removeMarkers();
    }
}
