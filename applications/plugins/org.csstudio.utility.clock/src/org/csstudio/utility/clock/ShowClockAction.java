package org.csstudio.utility.clock;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action connected to workbench menu action set for showing the view.
 *  @author Kay Kasemir
 */
public class ShowClockAction extends OpenViewAction
{
    public ShowClockAction()
    {
        super(ClockView.ID);
    }
}
