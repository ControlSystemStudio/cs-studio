/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.influxdb;

import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.csstudio.archive.influxdb.MetaTypes.StoreAs;
import org.csstudio.archive.writer.WriteChannel;

/** Channel information for channel in InfluxDB
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class InfluxDBWriteChannel implements WriteChannel
{
    final private String name;
    private Object meta = null;
    private StoreAs storeas = StoreAs.ARCHIVE_UNKNOWN;

    /** Initialize
     *  @param name Channel name
     */
    public InfluxDBWriteChannel(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "InfluxDBWriteChannel '" + name + "'";
    }

    /** @return Meta data or <code>null</code> */
    public Object getMetadata()
    {
        return meta;
    }

    public StoreAs getStorageType()
    {
        return storeas;
    }

    /** @param meta Current meta data of channel */
    public void setMetaData(final Object meta, final StoreAs storeas)
    {
        this.meta = meta;
        this.storeas = storeas;
    }

    public void setMetaData(MetaObject mo)
    {
        this.meta = mo.object;
        this.storeas = mo.storeas;
    }

    public String toLongString()
    {
        String metaclass;
        try
        {
            metaclass = meta.getClass().getName();
        }
        catch (Exception e)
        {
            metaclass = "";
        }

        return "InfluxDBWriteChannel '" + name + "' (" + storeas.name() + ": " + metaclass + ")";
    }

}
