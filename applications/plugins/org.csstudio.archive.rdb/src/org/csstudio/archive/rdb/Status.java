package org.csstudio.archive.rdb;

import org.csstudio.utility.rdb.StringID;

/** Status ID
 *  @author Kay Kasemir
 */
public class Status extends StringID
{
	public Status(int id, String name)
	{
		super(id, name);
	}
	
    @Override
    @SuppressWarnings("nls")
    final public String toString()
    {
        return String.format("Status '%s' (%d)", getName(), getId());
    }
}
