package org.csstudio.debugging.rdbshell;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action to show RDB Shell
 *  plugin.xml places it in CSS menu bar.
 *  @author Kay Kasemir
 */
public class ShowRDBShellAction extends OpenViewAction
{
    public ShowRDBShellAction()
    {
        super(RDBShellView.ID);
    }
}
