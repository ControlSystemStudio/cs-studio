/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR_CTRL_Float;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.TIME;
import java.text.NumberFormat;
import org.epics.util.text.NumberFormats;
import org.epics.vtype.Display;

/**
 *
 * @author carcassi
 */
class VNumberMetadata<TValue extends TIME, TMetadata extends CTRL> extends VMetadata<TValue> implements Display {

    private final TMetadata metadata;
    private final boolean honorZeroPrecision;

    VNumberMetadata(TValue dbrValue, TMetadata metadata, JCAConnectionPayload connPayload) {
        super(dbrValue, connPayload);
        this.metadata = metadata;
        this.honorZeroPrecision = connPayload.getJcaDataSource().isHonorZeroPrecision();
    }

    @Override
    public Double getLowerDisplayLimit() {
        return (Double) metadata.getLowerDispLimit();
    }

    @Override
    public Double getLowerCtrlLimit() {
        return (Double) metadata.getLowerCtrlLimit();
    }

    @Override
    public Double getLowerAlarmLimit() {
        return (Double) metadata.getLowerAlarmLimit();
    }

    @Override
    public Double getLowerWarningLimit() {
        return (Double) metadata.getLowerWarningLimit();
    }

    @Override
    public String getUnits() {
        return metadata.getUnits();
    }

    @Override
    public NumberFormat getFormat() {
        int precision = -1;
        if (metadata instanceof PRECISION) {
            precision = ((PRECISION) metadata).getPrecision();
        }
        
        // If precision is 0 or less, we assume full precision
        if (precision < 0) {
            return NumberFormats.toStringFormat();
        } else if (precision == 0) {
            if (honorZeroPrecision) {
                return NumberFormats.format(0);
            } else {
                return NumberFormats.toStringFormat();
            }
        } else {
            return NumberFormats.format(precision);
        }
    }

    @Override
    public Double getUpperWarningLimit() {
        return (Double) metadata.getUpperWarningLimit();
    }

    @Override
    public Double getUpperAlarmLimit() {
        return (Double) metadata.getUpperAlarmLimit();
    }

    @Override
    public Double getUpperCtrlLimit() {
        return (Double) metadata.getUpperCtrlLimit();
    }

    @Override
    public Double getUpperDisplayLimit() {
        return (Double) metadata.getUpperDispLimit();
    }

}
