package org.csstudio.java.thread;

/** Cache statistics
 *  @author Kay Kasemir
 */
public class CacheStats
{
    final long hits, misses, expirations, total;

    /** Initialize
     *  @param hits
     *  @param misses
     *  @param expirations
     */
    CacheStats(final long hits, final long misses, final long expirations)
    {
        this.hits = hits;
        this.misses = misses;
        this.expirations = expirations;
        this.total = hits + misses + expirations;
    }

    /** @return hits */
    public long getHits()
    {
        return hits;
    }

    /** @return misses */
    public long getMisses()
    {
        return misses;
    }

    /** @return expirations */
    public long getExpirations()
    {
        return expirations;
    }

    /** @return total number of cache accesses */
    public long getTotal()
    {
        return total;
    }

    /** @return Info text suitable for display on web page */
    @Override
    public String toString()
    {
        if (total <= 0)
            return "Never used";
        final StringBuilder buf = new StringBuilder();
        buf.append("Cache hits=").append(hits).append(" (").append(hits*100/total).append("%), ");
        buf.append("misses=").append(misses).append(" (").append(misses*100/total).append("%), ");
        buf.append("expirations=").append(expirations).append(" (").append(expirations*100/total).append("%)");
        return buf.toString();
    }
}
