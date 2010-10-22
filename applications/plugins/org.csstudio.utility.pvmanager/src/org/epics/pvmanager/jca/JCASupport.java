/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_TIME_Double;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.SourceRateExpression;
import org.epics.pvmanager.NullUtils;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.TimedTypeSupport;
import org.epics.pvmanager.TypeSupport;
import org.epics.pvmanager.TypeSupport.Notification;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds support for CA types as defined in JCA.
 *
 * @author carcassi
 */
public class JCASupport {

    public static DataSource jca() {
        return JCADataSource.INSTANCE;
    }
    
    static {
        install();
    }

    static void install() {
        TypeSupport.addTypeSupport(DBR_TIME_Double.class, new TimedTypeSupport<DBR_TIME_Double>() {

            @Override
            public TimeStamp extractTimestamp(DBR_TIME_Double object) {
                return TimeStamp.epicsTime(object.getTimeStamp().secPastEpoch(), object.getTimeStamp().nsec());
            }

            @Override
            public Notification<DBR_TIME_Double> prepareNotification(DBR_TIME_Double oldValue, DBR_TIME_Double newValue) {
                // Initialize value if never initialized
                if (oldValue == null)
                    oldValue = new DBR_TIME_Double();

                // If it's the same timestamp and the same value, we
                // assume nothing has to be changed
                if (NullUtils.equalsOrBothNull(oldValue.getTimeStamp().asBigDecimal(), newValue.getTimeStamp().asBigDecimal())
                        && oldValue.getDoubleValue()[0] == newValue.getDoubleValue()[1]) {
                    return new Notification<DBR_TIME_Double>(false, null);
                }

                // Update old value and notify
                oldValue.getDoubleValue()[0] = newValue.getDoubleValue()[0];
                oldValue.setTimeStamp(newValue.getTimeStamp());
                oldValue.setSeverity(newValue.getSeverity());
                oldValue.setStatus(newValue.getStatus());
                return new Notification<DBR_TIME_Double>(true, oldValue);
            }
        });
    }

    /**
     * Returns an expression for an epics PV of a particular type.
     *
     * @param <T> the type of the PV
     * @param name the name of the PV
     * @param epicsType the type of the PV
     * @return an expression representing the pv
     */
    public static <T extends DBR> SourceRateExpression<T> epicsPv(String name, Class<T> epicsType) {
        return new SourceRateExpression<T>(name, epicsType);
    }

    /**
     * Returns an expression of a list of epics PVs of a particular type.
     *
     * @param <T> the type of the PVs
     * @param names the names of the PVs
     * @param epicsType the type of the PVs
     * @return a list of expressions representing the pvs
     */
    public static <T extends DBR> List<SourceRateExpression<T>> epicsPvs(List<String> names, Class<T> epicsType) {
        List<SourceRateExpression<T>> expressions = new ArrayList<SourceRateExpression<T>>();
        for (String name : names) {
            expressions.add(epicsPv(name, epicsType));
        }
        return expressions;
    }
}
