package org.csstudio.swt.chart;

import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.action.Action;

/** An Action for showing or hiding the button bar of the InteractiveChart.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  TODO packaging. Most users of the InteractiveChart will want to put this
 *       action in a toolbar or context menu, so this way we get consistency.
 *       On the other hand, this adds a JFace dependency to 
 *       org.csstudio.swt.chart...
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
              Messages.Chart_HideButtonBar : Messages.Chart_ShowButtonBar,
              Plugin.getImageDescriptor("icons/toolbar.gif")); //$NON-NLS-1$
        this.chart = chart;
        chart.addListener(this);
    }
    
    /** @see InteractiveChartListener */
    public void buttonBarChanged(boolean visible)
    {
        setText(visible ?
                Messages.Chart_HideButtonBar : Messages.Chart_ShowButtonBar);
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
