/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import org.csstudio.archive.influxdb.InfluxDBDataSource;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates an InfluxDB URL
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class InfluxDBRawReaderFactory implements ArchiveReaderFactory
{
    /** {@inheritDoc} */
    @Override
    public ArchiveReader getArchiveReader(final String encoded_url) throws Exception
    {
        // Format: influxdb-raw://host:port[?arg1=ARG1&arg2=ARG2...]
        // Valid args:
        // db=DBNAME (required) Using influxdb database DBNAME
        // user=USER (optional) login as USER
        // password=PASSWORD (optional) login as USER with PASSWORD


        final Activator instance = Activator.getInstance();
        if (instance == null)
            throw new Exception("InfluxDBArchiveReaderFactory requires Plugin infrastructure");

        InfluxDBDataSource ds = InfluxDBDataSource.decodeURL(encoded_url);

        synchronized (instance)
        {
            return new InfluxDBRawReader(ds.getURL(),
                    ds.getArg(InfluxDBDataSource.USER_KEY),
                    ds.getArg(InfluxDBDataSource.PASSW_KEY), ds.getArgRequired(InfluxDBDataSource.DB_KEY));
        }
    }
}
