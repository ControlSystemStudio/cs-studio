/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.server.Scan;

/** {@link IDataLogFactory} for the {@link MemoryDataLog}
 *  @author Kay Kasemir
 */
public class MemoryDataLogFactory implements IDataLogFactory
{
    /** Available scans. Length of list provides the next available <code>id</code> */
    final private static List<Scan> scans = new ArrayList<>();

    /** Map of scan IDs to logs */
    final private Map<Scan, DataLog> logs = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public synchronized Scan createDataLog(final String scan_name) throws Exception
    {
        final long id = scans.size() + 1;
        final Scan scan = new Scan(id, scan_name, Instant.now());
        scans.add(scan);
        logs.put(scan, new MemoryDataLog());
        return scan;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Scan[] getScans() throws Exception
    {
        return scans.toArray(new Scan[scans.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized DataLog getDataLog(final Scan scan) throws Exception
    {
        return logs.get(scan);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void deleteDataLog(final Scan scan) throws Exception
    {
        scans.remove(scan);
        logs.remove(scan);
    }
}
