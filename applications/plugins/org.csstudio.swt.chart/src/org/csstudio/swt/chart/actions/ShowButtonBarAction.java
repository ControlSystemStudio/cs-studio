/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.actions;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.InteractiveChartListener;
import org.csstudio.swt.chart.Messages;
import org.csstudio.swt.chart.axes.Marker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/** An Action for showing or hiding the button bar of the InteractiveChart.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *
 *  @author Kay Kasemir
 */
public class ShowButtonBarAction extends Action
    implements InteractiveChartListener
{
    private final InteractiveChart chart;

    public ShowButtonBarAction(InteractiveChart chart)
    {
        super(chart.isButtonBarVisible() ?
              Messages.Chart_HideButtonBar : Messages.Chart_ShowButtonBar);

        final ImageDescriptor icon = Activator.getImageDescriptor("icons/toolbar.gif"); //$NON-NLS-1$
        if (icon != null)
            setImageDescriptor(icon);
        this.chart = chart;
        chart.addListener(this);
    }

    /** @see InteractiveChartListener */
    @Override
    public void buttonBarChanged(boolean visible)
    {
        setText(visible ?
                Messages.Chart_HideButtonBar : Messages.Chart_ShowButtonBar);
    }

    /** @see InteractiveChartListener */
    public void addedMarker(int y_axis_index, Marker marker)
    {
        // Ignore
    }

    @Override
    public void run()
    {
        if (chart.isButtonBarVisible())
        {
            chart.showButtonBar(false);
            setText(Messages.Chart_ShowButtonBar);
        }
        else
        {
            chart.showButtonBar(true);
            setText(Messages.Chart_HideButtonBar);
        }
    }
}
