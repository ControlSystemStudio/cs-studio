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
import org.csstudio.scan.server.Scan;

/** {@link IDataLogFactory} for the {@link MemoryDataLog}
 *  @author Kay Kasemir
 */
public class DerbyDataLogFactory implements IDataLogFactory
{
    /** {@inheritDoc} */
	@Override
    public Scan createDataLog(final String scan_name) throws Exception
    {
		final DerbyDataLogger logger = new DerbyDataLogger();
		try
		{
			return logger.createScan(scan_name);
		}
		finally
		{
			logger.close();
		}
    }

    /** {@inheritDoc} */
	@Override
    public Scan[] getScans() throws Exception
    {
        final DerbyDataLogger logger = new DerbyDataLogger();
        try
        {
            return logger.getScans();
        }
        finally
        {
            logger.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public DataLog getDataLog(final Scan scan) throws Exception
    {
        final DerbyDataLogger logger = new DerbyDataLogger();
        try
        {
            if (logger.getScan(scan.getId()) != null)
                return new DerbyDataLog(scan.getId());
        }
        finally
        {
            logger.close();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void deleteDataLog(final Scan scan) throws Exception
    {
        final DerbyDataLogger logger = new DerbyDataLogger();
        try
        {
            logger.deleteDataLog(scan.getId());
        }
        finally
        {
            logger.close();
        }
    }
}
