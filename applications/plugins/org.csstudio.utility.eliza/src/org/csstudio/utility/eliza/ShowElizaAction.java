package org.csstudio.utility.eliza;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Menu action to open Eliza View
 *  @author Kay Kasemir
 */
public class ShowElizaAction extends OpenViewAction
{
    public ShowElizaAction()
    {
        super(ElizaView.ID);
    }
}
