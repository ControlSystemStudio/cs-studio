package org.csstudio.debugging.jmsmonitor;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action to show JMS Monitor.
 *  plugin.xml places it in CSS menu bar.
 *  @author Kay Kasemir
 */
public class ShowJMSMonitorAction extends OpenViewAction
{
    public ShowJMSMonitorAction()
    {
        super(JMSMonitorView.ID);
    }
}
