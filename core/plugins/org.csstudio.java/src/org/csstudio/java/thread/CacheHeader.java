package org.csstudio.java.thread;

import java.util.Date;

/** Helper for computing cache expiration info.
 *
 *  @author Kay Kasemir, Xihui Chen
 */
public class CacheHeader
{

    /** Time when entry was last updated */
    final private Date timestamp;

    /** Time when entry expires */
    final private Date expiration;

    /** Initialize
     *  @param seconds Seconds after which clients should no longer cache the HTML
     */
    public CacheHeader(final long seconds)
    {
        timestamp = new Date();
        expiration = new Date(timestamp.getTime() + seconds * 1000L);
    }

    /** Time when entry was last updated */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /** Time when entry expires */
    public Date getExpirationDate()
    {
        return expiration;
    }

}
