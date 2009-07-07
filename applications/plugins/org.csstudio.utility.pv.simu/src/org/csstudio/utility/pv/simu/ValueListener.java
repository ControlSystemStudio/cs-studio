package org.csstudio.utility.pv.simu;

/** Listener to a DynamicValue
 *  @author Kay Kasemir
 */
public interface ValueListener
{
    /** Notification of change in value
     *  @param value The value that changed
     */
    public void changed(Value value);
}
