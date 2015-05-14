/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.actions;

import java.util.logging.Level;

import org.csstudio.apputil.ui.dialog.TextInputDialog;
import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.csstudio.swt.chart.axes.Marker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;

/** An action that updates the text of selected markers.
 *  @author Kay Kasemir
 */
public class UpdateSelectedMarkersAction extends Action
{
    private Chart chart;

    /** Constructor */
    public UpdateSelectedMarkersAction(Chart chart)
    {
        this.chart = chart;
        setText(Messages.UpdateSelectedMarker);
        setToolTipText(Messages.UpdateSelectedMarker_TT);
        // In unit tests w/o Workbench this can fail
        try
        {
            setImageDescriptor(Activator.getImageDescriptor("icons/edit_marker.gif")); //$NON-NLS-1$
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.WARNING,
                    "UpdateSelectedMarkersAction cannot get image", ex); //$NON-NLS-1$
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
        for (int i=0;  i<chart.getNumYAxes();  ++i)
        {
            final Marker[] markers = chart.getYAxis(i).getMarkers();
            for (Marker marker : markers)
            {
                if (marker.isSelected())
                    update(marker);
            }
        }
        chart.redraw();
    }

    private void update(final Marker marker)
    {
        final TextInputDialog dlg = new TextInputDialog(chart.getShell(),
                Messages.UpdateSelectedMarker, Messages.UpdateSelectedMarker_TT, marker.getText());
        if (dlg.open() != Window.OK)
            return;
        marker.setText(dlg.getValue());
    }
}
