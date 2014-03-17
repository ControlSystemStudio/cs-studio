/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TimeStamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.AlarmSeverity;
import org.epics.util.time.Timestamp;

/**
 * Utilities to convert JCA types to VData types.
 *
 * @author carcassi
 */
class DataUtils {

    private static final Logger log = Logger.getLogger(DataUtils.class.getName());

    /**
     * Constant to convert epics seconds to UNIX seconds. It counts the number
     * of seconds for 20 years, 5 of which leap years. It does _not_ count the
     * number of leap seconds (which should have been 15).
     */
    static long TS_EPOCH_SEC_PAST_1970=631152000; //7305*86400;
    
    /**
     * Converts a JCA timestamp to an epics.util timestamp.
     * 
     * @param epicsTimeStamp the epics timestamp
     * @return a new epics.util timestamp
     */
    static org.epics.util.time.Timestamp timestampOf(gov.aps.jca.dbr.TimeStamp epicsTimeStamp) {
        if (epicsTimeStamp == null)
            return null;
        
        return org.epics.util.time.Timestamp.of(epicsTimeStamp.secPastEpoch() + TS_EPOCH_SEC_PAST_1970, 0)
                .plus(TimeDuration.ofNanos(epicsTimeStamp.nsec()));
    }

    /**
     * Converts an alarm severity from JCA to VData.
     *
     * @param severity the JCA severity
     * @return the VData severity
     */
    static AlarmSeverity fromEpics(Severity severity) {
        if (Severity.NO_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.NONE;
        } else if (Severity.MINOR_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.MINOR;
        } else if (Severity.MAJOR_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.MAJOR;
        } else if (Severity.INVALID_ALARM.isEqualTo(severity)) {
            return AlarmSeverity.INVALID;
        } else {
            return AlarmSeverity.UNDEFINED;
        }
    }

    // Creates the list of EPICS 3 status by
    // iterating over all Status defined in JCA
    private static final List<String> epicsPossibleStatus;
    static {
        List<String> mutableList = new ArrayList<String>();
        Status aStatus = Status.forValue(0);
        while (aStatus != null) {
            mutableList.add(aStatus.getName());
            aStatus = Status.forValue(aStatus.getValue() + 1);
        }
        epicsPossibleStatus = Collections.unmodifiableList(mutableList);
    }

    /**
     * Returns all the possible EPICS 3 status.
     *
     * @return all possible status
     */
    static List<String> epicsPossibleStatus() {
        return epicsPossibleStatus;
    }

    /**
     * Determines whether the timestamp represents good data or not. It
     * checks whether the seconds are either a UNIX 0 or a Epics 0.
     *
     * @param timeStamp a timestamp
     * @return false if timeStamp is null or represents a UNIX 0 or an Epics 0
     */
    static boolean isTimeValid(TimeStamp timeStamp) {
        if (timeStamp == null)
            return false;
        
        long sec = timeStamp.secPastEpoch() + TS_EPOCH_SEC_PAST_1970;
        if (sec == 0 || sec == TS_EPOCH_SEC_PAST_1970) {
            return false;
        }
        
        long nanosec = timeStamp.nsec();
        if (nanosec < 0 || nanosec > 999_999_999) {
            return false;
        }
        
        return true;
    }

}
