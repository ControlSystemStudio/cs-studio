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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 * DAL based implementation of the message abstraction of the AlarmService
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
@SuppressWarnings("unchecked")
public class AlarmMessageDALImpl implements IAlarmMessage {
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmMessageDALImpl.class);

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
    private final SimpleProperty _property;
    private final AnyData _anyDataOpt; // Opt: May be null

    /**
     * Create alarm message with the given condition and anydata.
     * @param property
     * @param anyData
     */
    public AlarmMessageDALImpl(@Nonnull final SimpleProperty property, @Nonnull final AnyData anyData) {
        _property = property;
        _anyDataOpt = anyData;

    }

    /**
     * Create alarm message with the given property. This one must be used if anydata is not available.
     *
     * @param property
     */
    public AlarmMessageDALImpl(@Nonnull final SimpleProperty property) {
        _property = property;
        _anyDataOpt = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final String getString(@Nonnull final String keyAsString) {
        AlarmMessageKey key = AlarmMessageKey.findKeyWithDefiningName(keyAsString);
        return (key == null) ? null : getString(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String getString(@Nonnull final AlarmMessageKey key) {
        String result = null;

        switch (key) {
            case EVENTTIME:
                result = retrieveEventtimeAsString();
                break;
            case NAME:
                result = retrieveNameAsString();
                break;
            case SEVERITY:
                result = retrieveSeverityAsString();
                break;
            case STATUS:
                result = retrieveStatusAsString();
                break;
            case FACILITY:
                // TODO MCL: there's currently no facility available from DAL
                // this could be retrieved from LDAP - necessary ??
                result = "NN";
                break;
            case HOST:
                result = retrieveHostnameAsString();
                break;
            //            case TEXT:
            //              // TODO MCL: this is actually the descriptor information from the channel
            //            	// this is not available by default - it could be retrieved from the channel - as an additional DAL request
            //            	// ... maybe too much effort ...
            //                break;
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
                result = "n.a.";
        }
        return result;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> result = new HashMap<String, String>();
        // TODO jp securely access incoming message
        for (AlarmMessageKey key : AlarmMessageKey.values()) {
            result.put(key.getDefiningName(), getString(key));
        }
        return result;

        // TODO jp What to do if message is undefined? Do not create alarm message, i.e. stop processing and ignore jms message?
        /*
         * if the value is noTimeStamp or Uninitialized or noMetaData
         * return null -> do NOT create message !
         *        String value = getString(key);
         *        if ( value!=null && !value.equals("noTimeStamp")&& !value.equals("Uninitialized") && !value.equals("noMetaData")) {
         */
    }

    @Override
    public final String toString() {
        return "DAL-AlarmMessage for " + getString(AlarmMessageKey.NAME) + ", " + getString(AlarmMessageKey.SEVERITY)
                + getString(AlarmMessageKey.STATUS);
    }

    @Nonnull
    private SimpleProperty getProperty() {
        return _property;
    }

    @Nonnull
    private DynamicValueCondition getCondition() {
        return getProperty().getCondition();
    }

    @CheckForNull
    private AnyData getAnyData() {
        return _anyDataOpt;
    }

    private boolean hasAnyData() {
        return _anyDataOpt != null;
    }

    @CheckForNull
    private MetaData getMetaData() {
        return getAnyData().getMetaData();
    }


    private boolean hasMetaData() {
        return hasAnyData() && (getAnyData().getMetaData() == null);
    }

    @Nonnull
    private String retrieveEventtimeAsString() {
        String result = "n.a.";
        Timestamp timestamp = getProperty().getCondition().getTimestamp();
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
            result = sdf.format(timestamp.getMilliseconds());
        }
        return result;
    }

    @Nonnull
    private String retrieveNameAsString() {
        return getProperty().getUniqueName();
    }

    @Nonnull
    private String retrieveSeverityAsString() {
        String result = "n.a.";
        if (getCondition().isMajor()) {
            result = "MAJOR";
        } else if (getCondition().isMinor()) {
            result = "MINOR";
        } else if (getCondition().isOK()) {
            result = "NO_ALARM";
        } else if (getCondition().isInvalid()) {
            result = "INVALID";
        }

        return result;
    }

    @Nonnull
    private String retrieveStatusAsString() {
        String result = "n.a.";
        if (hasAnyData()) {
            result = getAnyData().getSeverity().descriptionToString();
        }
        return result;
    }

    @Nonnull
    private String retrieveHostnameAsString() {
        // TODO MCL: we can get the host name from the DAL connection - available?
        String result = "n.a.";
        if (hasMetaData()) {
            result = getMetaData().getHostname();
            if (result == null) {
                result = "host undefined";
            }
        }
        return result;
    }

    @Nonnull
    private String retrieveValueAsString() {
        String result = "n.a.";
        if (hasAnyData()) {
            try {
                result = getAnyData().stringValue();
            } catch (Exception e) {
                result = "value undefined";
            }
        }
        return result;
    }

    @Nonnull
    private String retrieveApplicationIDAsString() {
        return APPLICATION_ID;
    }
}
