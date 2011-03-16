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

/** An action that removes all selected markers from the chart.
 *  @author Kay Kasemir
 */
public class RemoveSelectedMarkersAction extends Action
{
    private Chart chart;

    /** Constructor */
    public RemoveSelectedMarkersAction(Chart chart)
    {
        this.chart = chart;
        setText(Messages.RemoveSelectedMarker);
        setToolTipText(Messages.RemoveSelectedMarker_TT);
        // In unit tests w/o Workbench this can fail
        try
        {
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING,
                    "RemoveSelectedMarkersAction cannot get 'delete' image", ex); //$NON-NLS-1$
        }
    }

    /** Must be called to update the 'enabled' state of this action.
     *  <p>
     *  This action could listen to the chart
     *  and update when the chart changes.
     *  But that looks like a lot of effort.
     *  <p>
     *  Overriding isEnabled() didn't work, looks like it's
     *  not called _every_ time the context menu is shown,
     *  only sometimes.
     *  <p>
     *  So we need to call this from a popup menu's menuAboutToShow().
     */
    public void updateEnablement()
    {
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            if (chart.getYAxis(i).haveSelectedMarkers())
            {
                setEnabled(true);
                return;
            }
        setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        chart.setRedraw(false);
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            chart.getYAxis(i).removeSelectedMarkers();
        chart.setRedraw(true);
    }
}
