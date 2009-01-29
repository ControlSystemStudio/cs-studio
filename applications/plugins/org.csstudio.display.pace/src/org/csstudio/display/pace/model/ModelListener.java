package org.csstudio.display.pace.model;

/** Listener to Model changes
 *  @author Kay Kasemir
 *  
 *   reviewed by Delphy 01/28/09
 */
public interface ModelListener
{
    /** Notification of cell update.
     *  @param cell Cell that changed its value in any way:
     *              Received new data from PV,
     *              user updated the value, reset to original value, ...
     */
    void cellUpdate(Cell cell);
}
