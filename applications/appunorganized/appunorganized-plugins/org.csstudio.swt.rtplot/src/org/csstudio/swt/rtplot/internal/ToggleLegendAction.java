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

    public ToggleLegendAction(final RTPlot<?> plot)
    {
        super(Messages.Legend_Show, Action.AS_CHECK_BOX);
        this.setImageDescriptor(Activator.getIcon("legend"));
        this.setChecked(plot.isLegendVisible());
        this.plot = plot;
    }

    public void update()
    {
        setChecked(plot.isLegendVisible());
    }

    @Override
    public void run()
    {
        plot.showLegend(! plot.isLegendVisible());
    }
}
