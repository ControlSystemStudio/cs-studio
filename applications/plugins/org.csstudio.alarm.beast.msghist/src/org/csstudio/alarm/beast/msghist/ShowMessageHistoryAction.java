package org.csstudio.alarm.beast.msghist;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action connected to workbench menu action set for showing the view.
 *  @author Kay Kasemir
 */
public class ShowMessageHistoryAction extends OpenViewAction
{
    public ShowMessageHistoryAction()
    {
        super(MessageHistoryView.ID);
    }
}
