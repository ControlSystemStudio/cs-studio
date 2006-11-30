package org.csstudio.trends.databrowser.model;

/** Listener interface for the <code>ChartModel</code>.
 *  @author Kay Kasemir
 *  @see Model
 */
public interface ModelListener
{
    /** Invoked when the scan or update periods changed. */
    public void periodsChanged();
    
    /** Invoked when many entries were added, removed or changed. */
    public void entriesChanged();

    /** Invoked when an entry was added. */
    public void entryAdded(IModelItem new_item);

    /** Invoked when an item's look was changed.
     *  <p>
     *  This includes color, line width, axis on which it's
     *  plotted.
     *  The item needs to be redrawn, but there is no new data.
     */
    public void entryLookChanged(IModelItem item);
    
    /** Invoked when the archive config of an item was changed.
     *  <p>
     *  Need to get new archived data, then redraw.
     */
    public void entryArchivesChanged(IModelItem item);
    
    /** Invoked when an entry was removed. */
    public void entryRemoved(IModelItem removed_item);
}
