package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.action.Action;

public class InspectSamplesAction extends Action
{
    public InspectSamplesAction()
    {
        super(Messages.InspectSamples,
              Activator.getDefault().getImageDescriptor("icons/inspect.gif")); //$NON-NLS-1$
    }

    @Override
    public void run()
    {
        new OpenViewAction(SampleView.ID).run();
    }
}
