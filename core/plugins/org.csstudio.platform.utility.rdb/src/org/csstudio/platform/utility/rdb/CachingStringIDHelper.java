/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.utility.rdb;

import java.util.HashMap;
import java.util.Map;

/** A {@link StringIDHelper} that caches entries so that subsequent
 *  lookups for the same item by ID are faster.
 *  @author Kay Kasemir
 */
public class CachingStringIDHelper extends StringIDHelper
{
    /** Map items by ID. HashMap allows <code>values</code> entries, which
     *  we treat as if they are not in the map.
     */
    final private Map<Integer, StringID> by_id = new HashMap<Integer, StringID>();
    
    /** Construct helper
     *  @param rdb RDBUTil
     *  @param table Name of RDB table
     *  @param id_column Name of the ID column
     *  @param name_column Name of the Name column
     */
    public CachingStringIDHelper(final RDBUtil rdb,
            final String table, final String id_column,
            final String name_column)
    {
        super(rdb, table, id_column, name_column);
    }

    @Override
    public void dispose()
    {
        by_id.clear();
        super.dispose();
    }

    @Override
    public StringID find(final int id) throws Exception
    {
        StringID item = by_id.get(id);
        if (item == null)
            item = super.find(id);
        by_id.put(item.getId(), item);
        return item;
    }

    @Override
    public StringID find(final String name) throws Exception
    {
        final StringID item = super.find(name);
        by_id.put(item.getId(), item);
        return item;
    }

    @Override
    public StringID add(final String name) throws Exception
    {
        final StringID item = super.add(name);
        by_id.put(item.getId(), item);
        return item;
    }
}
