package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;

/** Action to open the SampleView
 *  @author Kay Kasemir
 */
public class InspectSamplesAction extends OpenViewAction
{
    public InspectSamplesAction()
    {
        super(SampleView.ID,
              Messages.InspectSamples,
              Activator.getDefault().getImageDescriptor("icons/inspect.gif")); //$NON-NLS-1$
    }
}
