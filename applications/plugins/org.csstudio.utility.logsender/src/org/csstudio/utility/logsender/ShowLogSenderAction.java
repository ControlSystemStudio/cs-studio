package org.csstudio.utility.logsender;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Show the LogSender view via menu entry
 *  @author Kay Kasemir
 */
public class ShowLogSenderAction extends OpenViewAction
{
    public ShowLogSenderAction()
    {
        super(LogSenderView.ID);
    }
}
