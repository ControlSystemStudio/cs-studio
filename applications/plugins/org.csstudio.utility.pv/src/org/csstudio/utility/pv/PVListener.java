package org.csstudio.utility.pv;

/** A listener for PV updates. 
 *  @author Kay Kasemir
 */
public interface PVListener
{
    /** Notification of a new value. */
    public void pvValueUpdate(PV pv);
    
    /** Notification of a PV disconnect. */
    public void pvDisconnected(PV pv);
}
