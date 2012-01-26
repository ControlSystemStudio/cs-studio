/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmMessageJMSImpl.java,v 1.4
 * 2010/04/28 07:58:00 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAL based implementation of the message abstraction of the AlarmService
 * This is an immutable class.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public final class AlarmMessageDALImpl implements IAlarmMessage {
    private static final String NOT_AVAILABLE = "n.a.";

    private static final Logger LOG = LoggerFactory.getLogger(AlarmMessageDALImpl.class);

    private static final String ERROR_MESSAGE = "Error analyzing DAL message";
    /**
     * format of the time string
     */
    private static final String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * application ID for this application
     */
    private static final String APPLICATION_ID = "CSS_AlarmService";

    // The message is based upon this data
    private final SimpleProperty<?> _property;
    private final AnyData _anyData;

    /**
     * Create alarm message with the given condition and anydata.
     * @param property
     * @param anyData
     */
    private AlarmMessageDALImpl(@Nonnull final SimpleProperty<?> property,
                                @Nonnull final AnyData anyData) {
        _property = property;
        _anyData = anyData;
    }

    public static boolean canCreateAlarmMessageFrom(@Nonnull final SimpleProperty<?> property,
                                                    @Nonnull final AnyData anyData) {
        // TODO (jpenning) define correctness of alarm message from DAL here

        /*
         * if the value is noTimeStamp or Uninitialized or noMetaData
         * return null -> do NOT create message !
         *        String value = getString(key);
         *        if ( value!=null && !value.equals("noTimeStamp")&& !value.equals("Uninitialized") && !value.equals("noMetaData")) {
         */

        return true;
    }

    @Nonnull
    public static IAlarmMessage newAlarmMessage(@Nonnull final SimpleProperty<?> property,
                                                @Nonnull final AnyData anyData) {
        assert canCreateAlarmMessageFrom(property, anyData) : "Alarm message cannot be created for "
                + property.getUniqueName();
        return new AlarmMessageDALImpl(property, anyData);
    }

    /**
     * {@inheritDoc}
     * CHECKSTYLE OFF: CyclomaticComplexity|MethodLength
     */
    @Override
    @Nonnull
    public String getString(@Nonnull final AlarmMessageKey key) {
        String result;
        switch (key) {
            case ACK:
                result = retrieveAckAsString();
                break;
            case EVENTTIME:
                result = retrieveEventtimeAsString();
                break;
            case NAME:
                result = retrieveNameAsString();
                break;
            case SEVERITY:
                result = retrieveSeverityAsString();
                break;
            case SEVERITY_OLD:
                result = retrieveSeverityOldAsString();
                break;
            case STATUS:
                result = retrieveStatusAsString();
                break;
            case STATUS_OLD:
                result = retrieveStatusOldAsString();
                break;
            case HOST_PHYS:
                result = retrieveHostPhysAsString();
                break;
            case HOST:
                result = retrieveHostnameAsString();
                break;
            case FACILITY:
                result = retrieveFacilityString();
                break;
            case TEXT:
                result = retrieveText();
                break;
            case TYPE:
                // The type is hard coded as an alarm event, because we registered for such a beast.
                result = "event";
                break;
            case VALUE:
                result = retrieveValueAsString();
                break;
            case APPLICATION_ID:
                result = retrieveApplicationIDAsString();
                break;
            default:
                LOG.error(ERROR_MESSAGE + ". getString called for undefined key : " + key);
                result = NOT_AVAILABLE;
        }
        return result;
    }
    // CHECKSTYLE ON: CyclomaticComplexity|MethodLength

    @Override
    @Nonnull
    public Map<String, String> getMap() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final AlarmMessageKey key : AlarmMessageKey.values()) {
            result.put(key.getDefiningName(), getString(key));
        }
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "JMS-AlarmMessage of type " + getString(AlarmMessageKey.TYPE) +
               " for " + getString(AlarmMessageKey.NAME) +
               ", Severity " + getSeverity() +
               ", Status " + getString(AlarmMessageKey.STATUS);
    }

    @Nonnull
    private SimpleProperty<?> getProperty() {
        return _property;
    }

    @Nonnull
    private DynamicValueCondition getCondition() {
        return getProperty().getCondition();
    }
    @Nonnull
    private String retrieveAckAsString() {
        return NOT_AVAILABLE;
    }

    @Nonnull
    private String retrieveEventtimeAsString() {
        String result = NOT_AVAILABLE;
        final Timestamp timestamp = getTimestamp();
        if (timestamp != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
            result = sdf.format(timestamp.getMilliseconds());
        }
        return result;
    }

    @CheckForNull
    private Timestamp getTimestamp() {
        return _anyData.getTimestamp();
    }

    @Nonnull
    private String retrieveNameAsString() {
        return getProperty().getUniqueName();
    }

    @Nonnull
    private String retrieveSeverityAsString() {
        return getSeverity().name();
    }
    @Nonnull
    private String retrieveSeverityOldAsString() {
        // TODO (jpenning) NYI
        return NOT_AVAILABLE;
    }

    @Nonnull
    private String retrieveStatusAsString() {
        final Severity severity = _anyData.getSeverity();
        return severity == null ? NOT_AVAILABLE : severity.descriptionToString();
    }
    @Nonnull
    private String retrieveStatusOldAsString() {
        // TODO (jpenning) NYI
        return NOT_AVAILABLE;
    }
    @Nonnull
    private String retrieveHostPhysAsString() {
        // TODO (jpenning) NYI
        return NOT_AVAILABLE;
    }

    @Nonnull
    private String retrieveHostnameAsString() {
        // TODO (jpenning) we can get the host name from the DAL connection - available?
        String result = NOT_AVAILABLE;

        final MetaData metaData = _anyData.getMetaData();
        if (metaData != null) {
            result = metaData.getHostname();
        }

        return result;
    }
    @Nonnull
    private String retrieveFacilityString() {
        // TODO (jpenning): there's currently no facility available from DAL
        // this could be retrieved from LDAP - necessary ??
        return NOT_AVAILABLE;
    }
    @Nonnull
    private String retrieveText() {
        // TODO (jpenning): this is actually the descriptor information from the channel
        // this is not available by default - it could be retrieved from the channel - as an additional DAL request
        // ... maybe too much effort ...
        return NOT_AVAILABLE;
    }

    @Nonnull
    private String retrieveValueAsString() {
        String result = NOT_AVAILABLE;

        try {
            result = _anyData.stringValue();
            // TODO (jpenning) : which kind of exception may come up?
        } catch (final Exception e) {
            result = "value undefined";
        }
        return result;
    }

    @Nonnull
    private String retrieveApplicationIDAsString() {
        return APPLICATION_ID;
    }

    /**
     * Maps the condition from the DAL event to the severity enum.
     *
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public EpicsAlarmSeverity getSeverity() {
        EpicsAlarmSeverity result = EpicsAlarmSeverity.UNKNOWN;

        final DynamicValueCondition condition = getCondition();
        if (condition.isMajor()) {
            result = EpicsAlarmSeverity.MAJOR;
        } else if (condition.isMinor()) {
            result = EpicsAlarmSeverity.MINOR;
        } else if (condition.isOK()) {
            result = EpicsAlarmSeverity.NO_ALARM;
        } else if (condition.isInvalid()) {
            result = EpicsAlarmSeverity.INVALID;
        }
        return result;
    }

    @Override
    @CheckForNull
    public Date getEventtime() {
        Date result = null;
        final Timestamp timestamp = getTimestamp();
        if (timestamp != null) {
            result = new Date(timestamp.getMilliseconds());
        }
        return result;
    }

    @Override
    @Nonnull
    public Date getEventtimeOrCurrentTime() {
        Date result = getEventtime();
        if (result == null) {
            result = new Date(System.currentTimeMillis());
        }
        return result;
    }

    @Override
    public boolean isAcknowledgement() {
        // The DAL implementation currently does not support alarm acknowledgment
        return false;
    }
}
