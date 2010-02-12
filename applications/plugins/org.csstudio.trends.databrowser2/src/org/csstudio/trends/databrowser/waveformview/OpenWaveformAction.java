package org.csstudio.trends.databrowser.waveformview;

import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;

/** Action to open the WaveformView
 *  @author Kay Kasemir
 */
public class OpenWaveformAction extends OpenViewAction
{
    public OpenWaveformAction()
    {
        super(WaveformView.ID,
              Messages.OpenWaveformView,
              Activator.getDefault().getImageDescriptor("icons/wavesample.gif")); //$NON-NLS-1$
    }
}
