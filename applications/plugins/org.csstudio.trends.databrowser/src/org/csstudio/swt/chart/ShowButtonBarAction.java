package org.csstudio.swt.chart;

import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.action.Action;

/** An Action for showing or hiding the button bar of the InteractiveChart.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  // TODO does not update if the button bar is modified from other sources
 *  @author Kay Kasemir
 */
public class ShowButtonBarAction extends Action
{
    private final InteractiveChart chart;

    public ShowButtonBarAction(InteractiveChart chart)
    {
        super(chart.getButtonBar().isVisible() ?
              Messages.Chart_HideButtonBar : Messages.Chart_ShowButtonBar,
              Plugin.getImageDescriptor("icons/toolbar.gif")); //$NON-NLS-1$
        this.chart = chart;
    }

    @Override
    public void run()
    {
        if (chart.getButtonBar().isVisible())
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
