/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.GR;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.TIME;

import java.text.NumberFormat;

import org.epics.util.text.NumberFormats;
import org.epics.vtype.Display;

/** Wrap DBR as VType
 *
 *  <p>Based on ideas from org.epics.pvmanager.jca, Gabriele Carcassi
 *  @author Kay Kasemir
 */
public class DBRAlarmTimeDisplayWrapper<T_DBR extends TIME> extends DBRAlarmTimeWrapper<T_DBR> implements Display
{
    final private GR metadata;
    private NumberFormat format = null;

    public DBRAlarmTimeDisplayWrapper(final GR metadata, final T_DBR dbr)
    {
        super(dbr);
        this.metadata = metadata;
    }
    
    @Override
    public Double getLowerDisplayLimit()
    {
        return metadata.getLowerDispLimit().doubleValue();
    }

    @Override
    public Double getLowerCtrlLimit()
    {
        if (metadata instanceof CTRL)
            return ((CTRL)metadata).getLowerCtrlLimit().doubleValue();
        return getLowerDisplayLimit();
    }

    @Override
    public Double getLowerAlarmLimit()
    {
        return metadata.getLowerAlarmLimit().doubleValue();
    }

    @Override
    public Double getLowerWarningLimit()
    {
        return metadata.getLowerWarningLimit().doubleValue();
    }

    @Override
    public String getUnits()
    {
        return metadata.getUnits();
    }

    @Override
    public synchronized NumberFormat getFormat()
    {
        if (format  == null)
        {
            if (metadata instanceof PRECISION)
            {
                final int precision = ((PRECISION) metadata).getPrecision();
                if (precision > 0)
                    format = NumberFormats.format(precision);
                else
                    format = NumberFormats.toStringFormat();
            }

        }
        return format;
    }

    @Override
    public Double getUpperWarningLimit()
    {
        return metadata.getUpperWarningLimit().doubleValue();
    }

    @Override
    public Double getUpperAlarmLimit()
    {
        return metadata.getUpperAlarmLimit().doubleValue();
    }

    @Override
    public Double getUpperCtrlLimit()
    {
        if (metadata instanceof CTRL)
            return ((CTRL)metadata).getUpperCtrlLimit().doubleValue();
        return getUpperDisplayLimit();
    }

    @Override
    public Double getUpperDisplayLimit()
    {
        return metadata.getUpperDispLimit().doubleValue();
    }
}
