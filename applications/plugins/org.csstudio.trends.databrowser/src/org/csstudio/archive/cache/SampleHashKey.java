package org.csstudio.archive.cache;

import org.csstudio.platform.util.ITimestamp;

/** Used to identify cached sample requests.
 *  @author Kay Kasemir
 */
class SampleHashKey
{
    private int key;
    private String name;
    private ITimestamp start;
    private ITimestamp end;
    private int request_type;
    private int request_parm;
    
    /** Construct a key from pieces that identify a sample request on a server.
     */
    SampleHashKey(int key, String name,
                  ITimestamp start, ITimestamp end, int request_type,
                  int request_parm)
    {
        this.key = key;
        this.name = name;
        this.start = start;
        this.end = end;
        this.request_type = request_type;
        this.request_parm = request_parm;
    }

    /** Keys are equal when all their pieces match. */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (! (obj instanceof SampleHashKey))
            return false;
        SampleHashKey o = (SampleHashKey) obj;
        return key == o.key    &&
               name.equals(o.name) &&
               start.equals(o.start) &&
               end.equals(o.end) &&
               request_type == o.request_type &&
               request_parm == o.request_parm;
    }

    @Override
    public int hashCode()
    {
        return key + name.hashCode() + start.hashCode() + end.hashCode()
            + request_type + request_parm;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "SampleHashKey : " + name
           + ", key " + key
           + " from " + start.toString() + " to " + end.toString();
    }
}