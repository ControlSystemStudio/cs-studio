package org.csstudio.display.pvtable.model;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.value.Value;

/** Interface to one entry in the PVListModel.
 * 
 *  A PV with saved value, which would typically be a "setpoint" PV
 *  and its value at some time.
 *  <p>
 *  In addition, there can be a readback PV, if one wants to log
 *  how well the readback matched the setpoint at the time of the snapshot.
 * 
 *  @see PVListModel
 *  
 *  @author Kay Kasemir
 */
public interface PVListEntry extends IProcessVariable
{
    /** Since entries can be removed from the model while there's still
     *  a pending redraw or new value from the network,
     *  users should check if this entry is still valid.
     * @return Returns <code>true</code> if this entry has already been disposed.
     */
    public boolean isDisposed();
    
    /** @return Returns true if selected. */
    public boolean isSelected();
    
    /** @return Returns the PV. */
    public PV getPV();

    /** @return Returns the saved_value. */
    public Value getSavedValue();    

    /** @return Returns the readback PV. */
    public PV getReadbackPV();

    /** @return Returns the saved readback value. */
    public Value getSavedReadbackValue();
}
