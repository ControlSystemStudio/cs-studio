package org.csstudio.display.pace.model;

/** Listener to Model changes
 *  @author Kay Kasemir
 */
public interface ModelListener
{
    /** Notification of cell update
     *  @param cell Cell that changed
     */
    void cellUpdate(Cell cell);

}
