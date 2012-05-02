/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.IDataLogFactory;
import org.csstudio.scan.log.MemoryDataLog;
import org.csstudio.scan.log.MemoryDataLogFactory;

/** {@link IDataLogFactory} for the {@link MemoryDataLog}
 *  @author Kay Kasemir
 */
public class DerbyDataLogFactory
// TODO Don't extend MemoryDataLog, use DerbyDataLog
extends MemoryDataLogFactory
implements IDataLogFactory
{
	@Override
    public long createDataLog(final String scan_name) throws Exception
    {
	    return super.createDataLog(scan_name);
    }

	@Override
    public DataLog getDataLog(final long scan_id) throws Exception
    {
	    return super.getDataLog(scan_id);
    }
}
