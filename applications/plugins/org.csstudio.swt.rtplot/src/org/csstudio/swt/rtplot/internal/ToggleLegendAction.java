package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.eclipse.jface.action.Action;

public class ToggleLegendAction<XTYPE extends Comparable<XTYPE>> extends Action {

    final private RTPlot<XTYPE> plot;

    public ToggleLegendAction(final RTPlot<XTYPE> plot, final boolean is_visible)
    {
        super(is_visible ? Messages.Legendbar_Hide : Messages.Legendbar_Show,
              Activator.getIcon("legend"));
        this.plot = plot;
    }

    public void updateText()
    {
        setText(plot.isLegendbarVisible() ? Messages.Legendbar_Hide : Messages.Legendbar_Show);
    }

    @Override
    public void run()
    {
        plot.showLegendbar(! plot.isLegendbarVisible());
    }
}
