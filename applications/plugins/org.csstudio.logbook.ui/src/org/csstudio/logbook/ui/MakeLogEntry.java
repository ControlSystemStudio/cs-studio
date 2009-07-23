package org.csstudio.logbook.ui;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Menu action for opening ELogEntryView.ID
 *  @author Kay Kasemir
 */
public class MakeLogEntry  extends OpenViewAction
{
    public MakeLogEntry()
    {
        super(ELogEntryView.ID);
    }
}
