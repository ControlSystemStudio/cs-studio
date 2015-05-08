package org.csstudio.java.thread;

import java.util.Date;

/** Entry in the {@link TimedCache}
 *
 *   @author Katia Danilova, Kay Kasemir
 *
 *   @param <VALUETYPE>
 */
public class TimedCacheEntry<VALUETYPE>
{
    final private VALUETYPE value;
    final private CacheHeader header;

    /** Initialize
     *  @param value Value of this entry
     *  @param valid_seconds Duration in seconds, how long this entry is valid
     */
    public TimedCacheEntry(final VALUETYPE value, final long valid_seconds)
    {
        this.value = value;
        header = new CacheHeader(valid_seconds);
    }

    /** @return Value of enty */
    public VALUETYPE getValue()
    {
        return value;
    }

    /** @return Chache header info: Time stamp, expiration time */
    public CacheHeader getCacheHeader()
    {
        return header;
    }

    /** @return <code>true</code> if entry is still valid */
    public boolean isStillValid()
    {
        final Date now = new Date();
        return now.before(header.getExpirationDate());
    }
}
