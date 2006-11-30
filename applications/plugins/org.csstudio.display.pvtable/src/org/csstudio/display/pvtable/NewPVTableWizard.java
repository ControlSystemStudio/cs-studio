package org.csstudio.display.pvtable;

import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.util.wizard.NewFileWizard;

/** File/New wizard for PVTable.
 * 
 *  @author Kay Kasemir
 */
public class NewPVTableWizard extends NewFileWizard
{
    /** Constructor for NewPVTableWizard. */
    public NewPVTableWizard()
    {
        super(Plugin.getDefault(), 
                "PV Table",
                "pv_table.xml",
                new PVListModel().getXMLContent());
    }
}
