package org.csstudio.display.pace.model;

/** Listener to Model changes
 *  @author Kay Kasemir
 *  
 *   reviewed by Delphy 01/28/09
 */
//TODO Model changes need to be explained  
// cell has userVaue ... isdirty ...
public interface ModelListener
{
    /** Notification of cell update
     *  @param cell Cell that changed
     */
    void cellUpdate(Cell cell);

}
