/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import java.util.HashMap;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.StringID;
import org.csstudio.platform.utility.rdb.StringIDHelper;

/** Caching RDB interface to severity info.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityCache
{
    /** Name used for default (empty) severities. */
	private static final String DEFAULT_NAME = "OK";

    /** RDB Helper. */
    final private StringIDHelper helper;

    /** Cache that maps names to severities */
    final private HashMap<String, Severity> cache_by_name =
        new HashMap<String, Severity>();

	/** Constructor */
	public SeverityCache(final RDBUtil rdb, final SQL sql)
	{
		helper = new StringIDHelper(rdb,
	        sql.severity_table, sql.severity_id_column, sql.severity_name_column);
	}
	
   /** Close prepared statements, clear cache. */
    public void dispose()
    {
        helper.dispose();
        cache_by_name.clear();
    }

    /** Add Severity to cache */
	public void memorize(final Severity severity)
	{
		cache_by_name.put(severity.getName(), severity);
	}

	/** Get severity by name.
	 *  @param name severity name
	 *  @return severity or <code>null</code>
	 *  @throws Exception on error
	 */
	public Severity find(String name) throws Exception
	{
		if (name.length() == 0)
			name = DEFAULT_NAME;
		// Check cache
		Severity severity = cache_by_name.get(name);
		if (severity != null)
			return severity;
		final StringID found = helper.find(name);
		if (found != null)
		{
        	severity = new Severity(found.getId(), found.getName());
            memorize(severity);
        }
        // else: Nothing found
        return severity;
	}
	
	/** Find or create a severity by name.
	 *  @param name Severity name
	 *  @return Severity
	 *  @throws Exception on error
	 */
	public Severity findOrCreate(String name) throws Exception
	{
    	if (name.length() == 0)
    		name = DEFAULT_NAME;
    	// Existing entry?
        Severity severity = find(name);
        if (severity != null)
            return severity;
        final StringID added = helper.add(name);        
        severity = new Severity(added.getId(), added.getName());
        memorize(severity);
        return severity;
	}
}
