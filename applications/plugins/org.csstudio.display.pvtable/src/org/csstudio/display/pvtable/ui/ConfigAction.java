package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.model.PVListModel;


/** Action that adds a new PV to the model.
 *  @author Kay Kasemir
 */
public class ConfigAction extends PVListModelAction
{
    public ConfigAction(PVListModel pv_list)
    {
        super(pv_list);
        setText("Config");
        setToolTipText("Configure PV Table");
        setImageDescriptor(Plugin.getImageDescriptor("icons/config.gif"));
    }

    @Override
    public void run()
    {
        PVListModel pv_list = getPVListModel();
        if (pv_list == null)
            return;
        
        ConfigDialog dlg = new ConfigDialog(null,
                pv_list.getDescription(),
                pv_list.getTolerance(),
                pv_list.getUpdatePeriod());
        if (dlg.open() == ConfigDialog.OK)
        {
            pv_list.setDescription(dlg.getDescription());
            pv_list.setTolerance(dlg.getTolerance());
            pv_list.setUpdatePeriod(dlg.getUpdatePeriod());
        }
    }
}
