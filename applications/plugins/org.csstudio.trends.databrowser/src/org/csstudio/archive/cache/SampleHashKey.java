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
    private Object request_parms[];
    
    /** Construct a key from pieces that identify a sample request on a server.
     */
    SampleHashKey(int key, String name,
                  ITimestamp start, ITimestamp end, int request_type,
                  Object request_parms[])
    {
        this.key = key;
        this.name = name;
        this.start = start;
        this.end = end;
        this.request_type = request_type;
        this.request_parms = request_parms;
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
               equalObjects(request_parms, o.request_parms);
    }
    
    /** @return <code>true</code> if given object arrays match by value. */
    private boolean equalObjects(final Object a[], final Object b[])
    {
        if (a.length != b.length)
            return false;
        for (int i=0; i<a.length; ++i)
            if (! a[i].equals(b[i]))
                return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        return key + name.hashCode() + start.hashCode() + end.hashCode()
            + request_type + request_parms.hashCode();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "SampleHashKey '" + name
           + "', key " + key
           + ", " + start.toString() + " - " + end.toString()
           + ", type " + request_type;
    }
}