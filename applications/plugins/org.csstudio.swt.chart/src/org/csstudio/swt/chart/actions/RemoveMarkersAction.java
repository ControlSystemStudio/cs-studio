package org.csstudio.swt.chart.actions;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
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
        // This will fail in unit tests w/o workbench
        try
        {
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        }
        catch (Throwable ex)
        {
            Activator.getLogger().warn("RemoveMarkersAction cannot get 'delete' icon"); //$NON-NLS-1$
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
        for (int i=0;  i<chart.getNumYAxes();  ++i)
            chart.getYAxis(i).removeMarkers();
    }
}
