package org.csstudio.platform.utility.rdb;

/** A name (string) with an ID.
 *  @author Kay Kasemir
 */
public class StringID
{
    final private int id;
    final private String name;

    /** Construct name with given ID */
    public StringID(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    /** @return ID */
    final public int getId()
    {
        return id;
    }

    /** @return name */
    final public String getName()
    {
        return name;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "'" + name + "' (" + id + ")";
    }
}
