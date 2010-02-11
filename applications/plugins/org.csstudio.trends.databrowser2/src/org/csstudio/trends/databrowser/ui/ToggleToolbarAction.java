package org.csstudio.trends.databrowser.ui;

import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.action.Action;

/** Action that shows/hides the XYGraph's toolbar
 *  @author Kay Kasemir
 */
public class ToggleToolbarAction extends Action
{
    final private ToolbarArmedXYGraph plot;

    public ToggleToolbarAction(final ToolbarArmedXYGraph plot)
    {
        super(Messages.Toolbar_Hide,
              Activator.getDefault().getImageDescriptor("icons/toolbar.gif")); //$NON-NLS-1$
        this.plot = plot;
    }

    @Override
    public void run()
    {
        if (plot.isShowToolbar())
        {
            plot.setShowToolbar(false);
            setText(Messages.Toolbar_Show);
        }
        else
        {
            plot.setShowToolbar(true);
            setText(Messages.Toolbar_Hide);
        }
    }
}
