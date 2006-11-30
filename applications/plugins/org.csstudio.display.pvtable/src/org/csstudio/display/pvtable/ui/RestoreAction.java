package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListModel;

/** Action that restores values from a snapshot.
 *  @author Kay Kasemir
 */
public class RestoreAction extends PVListModelAction
{
    public RestoreAction(PVListModel pv_list)
    {
        super(pv_list);
        setText("Restore");
        setToolTipText("Restore values from snapshot");
        setImageDescriptor(Plugin.getImageDescriptor("icons/restore.gif"));
    }

    @Override
    public void run()
    {
        PVListModel pv_list = getPVListModel();
        if (pv_list != null)
            pv_list.restore();
    }
}
