package org.csstudio.display.pvtable;

import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.display.pvtable.ui.editor.PVTableEditor;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;

/** Another application sent us a PV name via its popup menu.
 *  @author Kay Kasemir
 */
public class PVpopupAction extends ProcessVariablePopupAction
{    
    public void handlePVs(IProcessVariable pv_names[])
    {   
    	PVTableEditor editor = PVTableEditor.createPVTableEditor();
    	if (editor == null)
    		return;
        PVListModel model = editor.getModel();
        for (IProcessVariable pv : pv_names)
            model.addPV(pv.getName());
    }
}
