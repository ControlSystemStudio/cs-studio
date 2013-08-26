package org.csstudio.java.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Thread-safe Cache for anything that times out after some time.
 *
 *  @author Katia Danilova, Kay Kasemir
 *
 *  @param <KEYTYPE>
 *  @param <VALUETYPE>
 */
public class TimedCache<KEYTYPE, VALUETYPE>
{
    /** Map KEY to { VALUE, Date when put into map }
     *  KEYTYPE can be any type,
     *  VALUETYPE can be any type (types determined when class instance created)
     */
    final private Map<KEYTYPE, TimedCacheEntry<VALUETYPE>> map = new HashMap<KEYTYPE, TimedCacheEntry<VALUETYPE>>();

    /** How long items are considered 'valid' in seconds */
    final private long timeout_secs;

    /** Number of successful cache hists */
    private long hits = 0;

    /** Number of failed cache hists */
    private long misses = 0;

    /** Number of entries that expired */
    private long exirations = 0;

    /** Initialize cache
     *  @param timeout_secs How long items are considered 'valid' in seconds
     */
    public TimedCache(final long timeout_secs)
    {
        this.timeout_secs = timeout_secs;
    }

    /** @return Cache statistics */
    public synchronized CacheStats getCacheStats()
    {
        return new CacheStats(hits, misses, exirations);
    }

    /** Get entry from cache
     *  @param key
     *  @return Cached entry or <code>null</code> when not found or timed out
     */
    public synchronized TimedCacheEntry<VALUETYPE> getEntry(KEYTYPE key)
    {
        final TimedCacheEntry<VALUETYPE> entry = map.get(key);
        // Is there a matching entry?
        if (entry == null)
        {
            ++misses;
            return null;
        }
        // Is it still valid?
        if (entry.isStillValid())
        {
            ++hits;
            return entry;
        }
        // Value is too old:
        ++exirations;
        map.remove(key);
        return null;
    }


    /** Get value of entry from cache
     *  @param key
     *  @return Cached value or <code>null</code> when not found or timed out
     */
    public VALUETYPE getValue(KEYTYPE key)
    {
        final TimedCacheEntry<VALUETYPE> entry = getEntry(key);
        if (entry == null)
            return null;
        return entry.getValue();
    }

    /** Add item to cache
     *  @param key
     *  @param value
     *  @return Cache entry
     */
    public synchronized TimedCacheEntry<VALUETYPE> remember(KEYTYPE key, VALUETYPE value)
    {
        final TimedCacheEntry<VALUETYPE> entry = new TimedCacheEntry<VALUETYPE>(value, timeout_secs);
        map.put(key, entry);
        return entry;
    }

    /** Use if need to get rid of all expired cache entries */
    public synchronized void cleanup()
    {
        final Iterator<KEYTYPE> keys = map.keySet().iterator();
        while (keys.hasNext())
        {
            final KEYTYPE key = keys.next();
            if (!map.get(key).isStillValid())
                map.remove(key);
        }
    }
}
