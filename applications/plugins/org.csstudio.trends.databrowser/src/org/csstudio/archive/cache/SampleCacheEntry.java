package org.csstudio.archive.cache;

import org.csstudio.archive.ArchiveValues;

/** One entry of the sample cache.
 *  <p>
 *  Each CachingArchiveServer remains a cache with items of this type.
 *  @author Kay Kasemir
 */
class SampleCacheEntry
{
    private SampleHashKey key;
    private ArchiveValues data;

    public SampleCacheEntry(SampleHashKey key, ArchiveValues data)
    {
        this.key = key;
        this.data = data;
    }

    public ArchiveValues getData()
    {   return data; }

    public SampleHashKey getKey()
    {   return key; }
}
