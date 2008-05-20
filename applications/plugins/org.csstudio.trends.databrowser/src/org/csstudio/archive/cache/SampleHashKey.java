package org.csstudio.archive.cache;

import org.csstudio.platform.data.ITimestamp;

/** Used to identify cached sample requests.
 *  @author Kay Kasemir
 */
class SampleHashKey
{
    final private int key;
    final private String name;
    final private ITimestamp start;
    final private ITimestamp end;
    final private String request_type;
    final private Object request_parms[];
    final private int hash_code;
    
    /** Construct a key from pieces that identify a sample request */
    SampleHashKey(final int key, final String name,
            final ITimestamp start, final ITimestamp end,
            final String request_type,
            final Object request_parms[])
    {
        this.key = key;
        this.name = name;
        this.start = start;
        this.end = end;
        this.request_type = request_type;
        this.request_parms = request_parms;
        // Compute hash code once because it's relatively expensive
        hash_code = key + name.hashCode() + start.hashCode() + end.hashCode()
            + request_type.hashCode();
    }

    /** Keys are equal when all their pieces match. */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (! (obj instanceof SampleHashKey))
            return false;
        final SampleHashKey o = (SampleHashKey) obj;
        return hash_code == o.hash_code &&
               key == o.key &&
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
        return hash_code;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "'" + name
           + "', key " + key
           + ", " + start.toString() + " - " + end.toString()
           + ", as " + request_type + " (" + hash_code + ")";
    }
}