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

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.alarm.IComparableAlarm;


/**
 * Represents the EPICS alarm types as of epics version 3.14.12.rc1 in dbStatic/alarm.h
 *
 * The EPICS alarm concept seems to match into the (CSS) general alarm abstraction by
 * mapping its two fields 'severity' and 'status' into the dedicated object.
 *
 * Epics alarms are comparable by their severity field.
 *
 * TODO (bknerr, jpenning, jhatje, bschoeneburg) : Epics Alarm Abstraction Problem
 *
 * 1 Some of the possible status from alarm.h are apparently not used
 * 2 Some status capture the very same information as severity does
 * 3 Some status' information overlaps,
 * 4 Some status are not defined for certain data types
 * (5) Some status have been introduced for CSS code only (is that good or bad or doesn't matter?)
 * (6) NO_ALARM
 *
 * Examples:
 * Status                      Severity
 *
 * NO_ALARM                    (->NO_ALARM) : (in principle, just learned there's an exception
 * LO, HI                      (->MINOR)
 * LOLO, HIHI                  (->MAJOR)
 * disconnected     (->?)      :used in the archiver code of
 * Archive_Disabled (?=Disable) (->?)      :TODO (bknerr) is 'Archive_Disabled' match any epics value, ask bernd.
 *
 * For non-numerical data types LO,HI,LOLO,HIHI isn't defined, hence, not possible.
 * (TODO (bknerr) : Checked for plausibility  by the {@link EpicsSystemVariable<T>} on setting it's alarm.
 *
 * @author Bastian Knerr
 */
public class EpicsAlarm implements IComparableAlarm<EpicsAlarm> {

    private final EpicsAlarmSeverity _severity;
    private final EpicsAlarmStatus _status;

    /**
     * Constructor.
     * @param sev the alarm severity
     * @param st the alarm status
     */
    public EpicsAlarm(@Nonnull final EpicsAlarmSeverity sev,
                      @Nonnull final EpicsAlarmStatus st) {

        // TODO (bknerr) : plausibility tests necessary?

        _severity = sev;
        _status = st;
    }

    /**
     * Getter for status.
     * @return the status.
     */
    @Nonnull
    public EpicsAlarmStatus getStatus() {
        return _status;
    }

    /**
     * Returns {@code true} if this severity is an actual alarm severity.
     *
     * @return whether this alarm is an actual alarm severity.
     */
//    public boolean isAlarm() {
//        return _isAlarm;
//    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compareAlarmTo(@Nonnull final EpicsAlarm other) {

        return _severity.compareSeverityTo(other._severity);
    }

}
