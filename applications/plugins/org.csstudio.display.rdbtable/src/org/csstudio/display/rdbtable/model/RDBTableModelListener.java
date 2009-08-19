package org.csstudio.display.rdbtable.model;

/** Listener to the RDBTableModel
 *  @author Kay Kasemir
 */
public interface RDBTableModelListener
{
    /** @param row Row that changed its values */
    public void rowChanged(RDBTableRow row);

    /** @param new_row Row that was added to the model */
    public void newRow(RDBTableRow new_row);
}
