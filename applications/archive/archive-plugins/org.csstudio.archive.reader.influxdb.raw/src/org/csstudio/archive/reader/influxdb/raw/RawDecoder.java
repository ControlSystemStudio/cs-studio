/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.reader.influxdb.raw;

import java.time.Instant;

import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVString;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** Decode values into VType
 *  @author Megan Grodowitz
 */
public class RawDecoder extends AbstractInfluxDBValueDecoder
{
    private final AbstractInfluxDBValueLookup vals;
    private final String fieldname;

    public RawDecoder(final AbstractInfluxDBValueLookup vals, final String fieldname) {
        this.vals = vals;
        this.fieldname = fieldname;
    }

    public static class Factory extends AbstractInfluxDBValueDecoder.Factory {

        private String fieldname;

        Factory(final String fieldname) {
            this.fieldname = fieldname;
        }

        @Override
        public AbstractInfluxDBValueDecoder create(AbstractInfluxDBValueLookup vals) {
            return new RawDecoder(vals, fieldname);
        }
    }

    @Override
    public VType decodeSampleValue() throws Exception
    {
        final Display display = ValueFactory.newDisplay(0.0, 0.0, 0.0, "double", NumberFormats.format(8), 10.0, 10.0,
                10.0, 0.0, 10.0);
        final AlarmSeverity severity = AlarmSeverity.UNDEFINED;
        final String status = "OK";
        final Instant time = InfluxDBUtil.fromInfluxDBTimeFormat(vals.getValue("time"));

        Object val = vals.getValue(fieldname);
        if (val == null) {
            throw new Exception("Did not find field: " + fieldname);
        }

        Double dbl = null;
        String str = null;
        try {
            dbl = Double.class.cast(val);
        } catch (Exception e) {
            try {
                dbl = Double.valueOf(val.toString());
            } catch (Exception e1) {
                str = val.toString();
            }
        }

        if (dbl != null)
            return new ArchiveVNumber(time, severity, status, display, dbl);

        return new ArchiveVString(time, severity, status, str);
    }


}
