/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.epics.alarm;

import gov.aps.jca.dbr.Status;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

/**
 * Status in EPICS as from  version 3.14.12.rc1 in
 * dbStatic/alarm.h
 *
 * Corresponds to goc.aps.jca.dbr.Status (which is a sort of java enum pre-build based on
 * int value fields and hashmaps).
 *
 * @author bknerr
 * @since 19.11.2010
 */
public enum EpicsAlarmStatus {
    NO_ALARM,
    READ,
    WRITE,
    HIHI,
    HIGH,
    LOLO,
    LOW,
    STATE,
    COS,
    COMM,
    TIMEOUT,
    HWLIMIT,
    CALC,
    SCAN,
    LINK,
    SOFT,
    BADSUB,
    UDF,
    DISABLE,
    SIMM,
    READACCESS,
    WRITEACCESS,
    UNKNOWN; // Added deliberately to allow for handling changes during epics evolution

    /**
     * List of status by integer code, the index in the list corresponds to the JCA code.
     * @see Status
     */
    private static List<EpicsAlarmStatus> STATUS_BY_CODE =
        Lists.newArrayList(NO_ALARM,
                           READ,
                           WRITE,
                           HIHI,
                           HIGH,
                           LOLO,
                           LOW,
                           STATE,
                           COS,
                           COMM,
                           TIMEOUT,
                           HWLIMIT,
                           CALC,
                           SCAN,
                           LINK,
                           SOFT,
                           BADSUB,
                           UDF,
                           DISABLE,
                           SIMM,
                           READACCESS,
                           WRITEACCESS,
                           UNKNOWN);


    /**
     * Converts a string representation of a status to an EpicsAlarmStatus.
     * Note, that unlike the {@link EpicsAlarmStatus#valueOf(String)} method, this method will never
     * throw an {@link IllegalArgumentException}. If there is no severity value for
     * the given string, this method will return {@link EpicsAlarmSeverity#UNKNOWN}.
     *
     * @param statusStr the status represented as a string value.
     * @return the status.
     */
    @Nonnull
    public static EpicsAlarmStatus parseStatus(@CheckForNull final String statusStr) {
        if (statusStr == null) {
            return UNKNOWN;
        }
        try {
            return valueOf(statusStr);
        } catch (final IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * Returns the DESY severity according to code taken from {@link Status}.
     *
     * If the code is not known (not in {0,...,21}) {@link EpicsAlarmStatus#UNKNOWN} is
     * returned.
     *
     * @param code the JCA integer code
     * @return the DESY alarm status
     */
    @Nonnull
    public static EpicsAlarmStatus valueOf(@Nonnull final Status status) {
        return byJCACode(status.getValue());
    }

    @Nonnull
    private static EpicsAlarmStatus byJCACode(final int code) {
        if (code < 0 || code >= STATUS_BY_CODE.size()) {
            return UNKNOWN;
        }
        return STATUS_BY_CODE.get(code);
    }
}
