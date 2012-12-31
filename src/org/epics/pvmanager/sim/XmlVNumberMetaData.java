/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import java.text.NumberFormat;
import javax.xml.bind.annotation.XmlAttribute;
import org.epics.pvmanager.vtype.Display;

/**
 *
 * @author carcassi
 */
class XmlVNumberMetaData extends XmlVMetaData implements Display {

    @XmlAttribute
    String units;
    @XmlAttribute
    Double lowerDisplayLimit;
    @XmlAttribute
    Double lowerCtrlLimit;
    @XmlAttribute
    Double lowerAlarmLimit;
    @XmlAttribute
    Double lowerWarningLimit;
    @XmlAttribute
    Double upperWarningLimit;
    @XmlAttribute
    Double upperAlarmLimit;
    @XmlAttribute
    Double upperCtrlLimit;
    @XmlAttribute
    Double upperDisplayLimit;

    @Override
    public Double getLowerDisplayLimit() {
        return lowerDisplayLimit;
    }

    @Override
    public Double getLowerCtrlLimit() {
        return lowerCtrlLimit;
    }

    @Override
    public Double getLowerAlarmLimit() {
        return lowerAlarmLimit;
    }

    @Override
    public Double getLowerWarningLimit() {
        return lowerWarningLimit;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public NumberFormat getFormat() {
        // TODO fix
        return NumberFormat.getNumberInstance();
    }

    @Override
    public Double getUpperWarningLimit() {
        return upperWarningLimit;
    }

    @Override
    public Double getUpperAlarmLimit() {
        return upperAlarmLimit;
    }

    @Override
    public Double getUpperCtrlLimit() {
        return upperCtrlLimit;
    }

    @Override
    public Double getUpperDisplayLimit() {
        return upperDisplayLimit;
    }
}
