package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.swt.chart.Chart;
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
        setText(Messages.RemoveClosestMarker);
        setToolTipText(Messages.RemoveClosestMarker_TT);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
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

    @Override
    public void run()
    {
        chart.setRedraw(false);
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            chart.getYAxis(i).removeSelectedMarkers();
        chart.setRedraw(true);
    }
}
