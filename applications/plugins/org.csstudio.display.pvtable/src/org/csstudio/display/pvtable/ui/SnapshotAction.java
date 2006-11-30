package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListModel;

/** Action that takes a snapshot of current values.
 *  @author Kay Kasemir
 */
public class SnapshotAction extends PVListModelAction
{
	public SnapshotAction(PVListModel pv_list)
	{
		super(pv_list);
		setText("Snapshot");
		setToolTipText("Take snapshot of current values");
		setImageDescriptor(Plugin.getImageDescriptor("icons/snapshot.gif"));
	}

	@Override
	public void run()
    {
	    PVListModel pv_list = getPVListModel();
	    if (pv_list != null)
            pv_list.takeSnapshot();
    }
}
