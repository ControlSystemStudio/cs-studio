/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal;

import java.util.HashMap;

import org.csstudio.archive.rdb.Status;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.StringID;
import org.csstudio.platform.utility.rdb.StringIDHelper;

/** Caching RDB interface to status info.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StatusCache
{
    /** Name used for default (empty) status strings. */
	private static final String DEFAULT_NAME = "OK";

	/** Helper. */
	final private StringIDHelper helper;
	
    /** Cache that maps names to stati */
    final private HashMap<String, Status> cache_by_name =
        new HashMap<String, Status>();
    
	/** Cache that maps IDs to stati */
    final private HashMap<Integer, Status> cache_by_id =
        new HashMap<Integer, Status>();

	/** Constructor */
	public StatusCache(final RDBUtil rdb, final SQL sql)
	{
	    helper = new StringIDHelper(rdb,
	        sql.status_table, sql.status_id_column, sql.status_name_column);
	}
	
	/** Close prepared statements, clear cache. */
    public void dispose()
    {
        helper.dispose();
        cache_by_name.clear();
        cache_by_id.clear();
    }

    /** Add Status to cache */
	public void memorize(final Status status)
	{
		cache_by_name.put(status.getName(), status);
		cache_by_id.put(status.getId(), status);
	}

	/** Get status by name.
	 *  @param name status name
	 *  @return status or <code>null</code>
	 *  @throws Exception on error
	 */
	private Status find(String name) throws Exception
	{
		if (name.length() == 0)
			name = DEFAULT_NAME;
		// Check cache
		Status status = cache_by_name.get(name);
		if (status != null)
			return status;
		final StringID found = helper.find(name);
		if (found != null)
		{
        	status = new Status(found.getId(), found.getName());
            memorize(status);
        }
        // else: Nothing found
        return status;
	}
	
	/** Get status by ID.
	 *  @param id Status ID
	 *  @return Status or <code>null</code>
	 *  @throws Exception on error
	 */
	public Status find(final int id) throws Exception
	{
		// Check cache
		Status status = cache_by_id.get(id);
		if (status != null)
			return status;
        final StringID found = helper.find(id);
        if (found != null)
        {
            status = new Status(found.getId(), found.getName());
            memorize(status);
        }
        // else: Nothing found
        return status;
	}

	/** Find or create a status by name.
	 *  @param name Status name
	 *  @return Status
	 *  @throws Exception on error
	 */
	public Status findOrCreate(String name) throws Exception
	{
    	if (name.length() == 0)
    		name = DEFAULT_NAME;
    	// Existing entry?
        Status status = find(name);
        if (status != null)
            return status;
        final StringID added = helper.add(name);
        status = new Status(added.getId(), added.getName());
        memorize(status);
        return status;
	}
}
