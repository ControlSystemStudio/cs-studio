package org.csstudio.trends.databrowser.model;

/** Listener interface for the <code>ChartModel</code>.
 *  @author Kay Kasemir
 *  @see Model
 */
public interface ModelListener
{
    /** Invoked when the start or end specifications changed.
     *  @see Model#getStartSpecification()
     */
    public void timeSpecificationsChanged();

    /** Invoked when the start or end times changed.
     *  @see Model#getStartTime()
     */
    public void timeRangeChanged();

    /** Invoked when the scan, update period or ring buffer size changed. */
    public void samplingChanged();
    
    /** Invoked when many entries were added, removed or changed. */
    public void entriesChanged();

    /** Invoked when an entry was added. */
    public void entryAdded(IModelItem new_item);

    /** Invoked when an item's configuration was changed.
     *  <p>
     *  This includes color, line width, axis on which it's
     *  plotted.
     *  The item needs to be redrawn, and the model is 'dirty',
     *  but there is no new data.
     */
    public void entryConfigChanged(IModelItem item);
    
    /** Invoked when an item's meta data was changed.
     *  <p>
     *  This includes the engineering units.
     *  The item needs to be redrawn,
     *  but there is no new data,
     *  and nothing needs to be saved.
     */
    public void entryMetaDataChanged(IModelItem item);    
    
    /** Invoked when the archive config of an item was changed.
     *  <p>
     *  Need to get new archived data, then redraw.
     */
    public void entryArchivesChanged(IModelItem item);
    
    /** Invoked when an entry was removed. */
    public void entryRemoved(IModelItem removed_item);
}
