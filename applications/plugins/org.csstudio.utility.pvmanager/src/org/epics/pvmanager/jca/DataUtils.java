/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.epics.pvmanager.data.AlarmSeverity;

/**
 * Utilities to convert JCA types to VData types.
 *
 * @author carcassi
 */
class DataUtils {

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
     * Converts a JCA status to a VData status.
     *
     * @param status JCA status
     * @return VData status
     */
    static Set<String> fromEpics(Status status) {
        if (status == null)
            return Collections.emptySet();
        return Collections.singleton(epicsPossibleStatus().get(status.getValue()));
    }

}
