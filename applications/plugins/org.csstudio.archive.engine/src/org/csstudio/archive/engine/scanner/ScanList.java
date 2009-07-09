package org.csstudio.archive.engine.scanner;

import java.util.ArrayList;

import org.csstudio.platform.logging.CentralLogger;

/** A Scan list scans a list of channels at a given rate.
 *  <p>
 *  Only the {@link Scanner} is supposed to modify and scan
 *  the scan list, so many methods are package-scoped.
 *  @author Kay Kasemir
 */
public class ScanList implements Scheduleable
{
    /** Scan period in seconds */
    final private long scan_period_millis;
    
    /** Items to scan */
    final private ArrayList<Runnable> items = new ArrayList<Runnable>();
    
    private long next_due_time = System.currentTimeMillis();
    
    /** Construct scan list.
     *  @param scan_period Scan period in seconds
     */
    public ScanList(final double scan_period)
    {
        scan_period_millis = (long)(scan_period * 1000.0);
        // Schedule initial run
        next_due_time = System.currentTimeMillis() + scan_period_millis;
    }

    /** @return Scan period in seconds */
    public final double getPeriod()
    {
        return scan_period_millis / 1000.0;
    }
    
    /** {@inheritDoc} */
    public boolean isDueAtAll()
    {
        return items.size() > 0;
    }

    /** {@inheritDoc} */
    public long getNextDueTime()
    {
        if (items.size() == 0)
            throw new Error(toString() + " never due"); //$NON-NLS-1$
        return next_due_time;
    }

    /** Add an item to the scan list */
    void add(final Runnable item)
    {
        items.add(item);
    }

    /** Remove an item from the scan list.
     *  @return <code>true</code> if found and removed
     */
    boolean remove(final Runnable item)
    {
        return items.remove(item);
    }
    
    /** @return Number of items on scan list */
    public int size()
    {
        return items.size();
    }
    
    /** @return Item with given index from the scan list */
    public Runnable get(final int index)
    {
        return items.get(index);
    }

    /** Scan all items on this scan list.
     *  Doesn't care if they're due or not,
     *  that's up to the code that invokes this.
     */
    void scanItems()
    {
        final long start = System.currentTimeMillis();
        // Scan all items
        for (Runnable item : items)
        {
            try
            {
                item.run();
            }
            catch (Throwable ex)
            {
                CentralLogger.getInstance().getLogger(this).error(toString() + " scan error", ex); //$NON-NLS-1$
            }
        }
        // Determine next due time relative to start,
        // swallowing the time used to scan the items.
        next_due_time = start + scan_period_millis;
    }

    @Override
    public String toString()
    {
        return "ScanList " + getPeriod(); //$NON-NLS-1$
    }
}
