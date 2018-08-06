package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.jface.action.Action;

/** Action to hide/show legend.
 *
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kunal Shroff - Original authos
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ToggleLegendAction extends Action
{
    final private RTPlot<?> plot;

    public ToggleLegendAction(final RTPlot<?> plot, final boolean is_visible)
    {
        super(is_visible ? Messages.Legend_Hide : Messages.Legend_Show,
              Activator.getIcon("legend"));
        this.plot = plot;
    }

    public void updateText()
    {
        setText(plot.isLegendVisible() ? Messages.Legend_Hide : Messages.Legend_Show);
    }

    @Override
    public void run()
    {
        plot.showLegend(! plot.isLegendVisible());
    }
}
