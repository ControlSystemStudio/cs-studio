/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.math.BigDecimal;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.epics.pvmanager.util.TimeStamp;

/**
 *
 * @author carcassi
 */
class XmlTimeStampAdapter extends XmlAdapter<BigDecimal, TimeStamp> {

    @Override
    public TimeStamp unmarshal(BigDecimal v) throws Exception {
        return TimeStamp.time(v.longValue(), v.remainder(new BigDecimal(1)).scaleByPowerOfTen(9).longValue());
    }

    @Override
    public BigDecimal marshal(TimeStamp v) throws Exception {
        return new BigDecimal(v.getNanoSec()).scaleByPowerOfTen(-9).add(new BigDecimal(v.getSec()));
    }

}
