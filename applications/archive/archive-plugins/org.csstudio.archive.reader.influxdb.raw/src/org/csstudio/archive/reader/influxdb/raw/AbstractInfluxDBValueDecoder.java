/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import org.diirt.vtype.VType;

/**
 * Base for Value Decoders that read from the InfluxDB
 *
 * @author Megan Grodowitz (InfluxDB)
 */
public abstract class AbstractInfluxDBValueDecoder {

    public static abstract class Factory {
        public abstract AbstractInfluxDBValueDecoder create(AbstractInfluxDBValueLookup vals);
    }

    public abstract VType decodeSampleValue() throws Exception;
}
