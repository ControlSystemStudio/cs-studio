/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import org.csstudio.archive.influxdb.MetaTypes.MetaObject;

/**
 * Base for Value Tables that read from the InfluxDB
 *
 * @author Megan Grodowitz (InfluxDB)
 */
@SuppressWarnings("nls")
abstract public class AbstractInfluxDBValueLookup
{
    public abstract Object getValue(String colname) throws Exception;

    public abstract boolean hasValue(String colname);

    public abstract MetaObject getMeta();
}
