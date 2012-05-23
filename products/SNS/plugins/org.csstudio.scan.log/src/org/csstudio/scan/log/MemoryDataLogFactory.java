/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/** {@link IDataLogFactory} for the {@link MemoryDataLog}
 *  @author Kay Kasemir
 */
public class MemoryDataLogFactory implements IDataLogFactory
{
    /** Provides the next available <code>id</code> */
    final private static AtomicLong ids = new AtomicLong();

    /** Map of scan IDs to logs */
    final private Map<Long, DataLog> logs = new HashMap<Long, DataLog>();

    /** {@inheritDoc} */
	@Override
	public long createDataLog(final String scan_name) throws Exception
	{
		final long id = ids.incrementAndGet();
		logs.put(id, new MemoryDataLog());
		return id;
	}

    /** {@inheritDoc} */
	@Override
	public DataLog getDataLog(final long scan_id) throws Exception
	{
		return logs.get(scan_id);
	}
}
