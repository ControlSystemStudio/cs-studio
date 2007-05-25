package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.swt.chart.Chart;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** An action that opens a view.
 *  @author Kay Kasemir
 */
public class RemoveMarkersAction extends Action
{
    private Chart chart;
    
    /** Construct the action for opening a view.
     *  @param part The parent part from which we get the 'page'.
     *  @param name Name to use for the action.
     *  @param ID The ID of the View to open.
     */
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
