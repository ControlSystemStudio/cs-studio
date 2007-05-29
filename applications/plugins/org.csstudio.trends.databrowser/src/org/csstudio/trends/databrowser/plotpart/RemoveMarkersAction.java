package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.swt.chart.Chart;
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
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    }
    
    @Override
    public void run()
    {
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            chart.getYAxis(i).removeMarkers();
    }
}
