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

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageException;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.Severity;

/**
 * DAL based implementation of the message abstraction of the AlarmService
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmMessageDALImpl implements IAlarmMessage {
    private static final String ERROR_MESSAGE = "Error analyzing DAL message";

    private final CentralLogger _log = CentralLogger.getInstance();

    private final AnyData _anyData;

    /**
     * Constructor.
     *
     * @param anyData
     */
    public AlarmMessageDALImpl(@Nonnull final AnyData anyData) {
        this._anyData = anyData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getString(@Nonnull final String keyAsString) throws AlarmMessageException {
        Key key = null;
        try {
            key = Key.valueOf(keyAsString);
        } catch (IllegalArgumentException e) {
            final String errorMessage = ERROR_MESSAGE + ". getString for undefined key-string : "
                    + keyAsString;
            _log.error(this, errorMessage);
            throw new AlarmMessageException(errorMessage);
        }
        return getString(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String getString(@Nonnull final Key key) {
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
                result = Application_ID;
                break;
            default:
                _log.error(this, ERROR_MESSAGE + ". getString called for undefined key : " + key);
                result = "No value, key " + key + " undefined";
        }
        return result;
    }

    @Nonnull
    private String retrieveEventtimeAsString() {
        String result;
        if (_anyData.getTimestamp() == null) {
            result = "noTimeStamp";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(JMS_DATE_FORMAT);
            result = sdf.format(_anyData.getTimestamp().getMilliseconds());
        }
        return result;
    }

    @Nonnull
    private String retrieveNameAsString() {
        String result;
        if (hasValidMetaData()) {
            result = "noMetaData";
        } else {
            result = _anyData.getMetaData().getName();
        }
        return result;
    }

    @Nonnull
    private String retrieveSeverityAsString() {
        String result;
        if (hasValidMetaData() || (_anyData.getSeverity() == null)) {
            result = "noMetaData";
        } else {
            result = getSeverityAsString(_anyData.getSeverity());
        }
        return result;
    }

    @Nonnull
    private String getSeverityAsString(@Nonnull final Severity severity) {
        // TODO jp use Severity enum here
        String result = null;

        if (severity.hasValue()) {
            result = severity.toString();
        }

        if (severity.isMajor()) {
            result = "MAJOR";
        } else if (severity.isMinor()) {
            result = "MINOR";
        } else if (severity.isOK()) {
            result = "NO_ALARM";
        } else if (severity.isInvalid()) {
            result = "INVALID";
        }
        return result;
    }

    @Nonnull
    private String retrieveStatusAsString() {
        return _anyData.getSeverity().descriptionToString();
    }

    @Nonnull
    private String retrieveHostnameAsString() {
        // TODO MCL: we can get the host name from the DAL connection - available?
        String result;
        if (hasValidMetaData()) {
            result = "noMetaData";
        } else {
            result = _anyData.getMetaData().getHostname();
            if (result == null) {
                result = "host undefined";
            }
        }
        return result;
    }

    @Nonnull
    private String retrieveValueAsString() {
        String result;
        if (_anyData.getSeverity().hasValue()) {
//            System.out.println("Severity " + _anyData.getSeverity().toString());
//            System.out.println("Severity " + _anyData.getSeverity().hasValue());
            try {
                result = _anyData.stringValue();
//                System.out.println("Value " + result);
            } catch (Exception e) {
//                System.out.println("Exception while analyzing _anyData.stringValue()" + e.getMessage());
                result = "-.--";
            }
        } else {
            result = "value undefined";
        }
        return result;
    }

    private boolean hasValidMetaData() {
        return _anyData.getMetaData() == null;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (Key key : Key.values()) {
            // TODO jp securely access incoming message
            result.put(key.name(), getString(key));
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
        return "DAL-AlarmMessage for " + getString(Key.NAME) + ", " + getString(Key.SEVERITY)
                + getString(Key.STATUS);
    }
}
