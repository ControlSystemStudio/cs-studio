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
import javax.jms.JMSException;
import javax.jms.MapMessage;

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
    public AlarmMessageDALImpl(final AnyData anyData) {
        this._anyData = anyData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(@Nonnull final String keyAsString) throws AlarmMessageException {
        Key key = null;
        try {
            key = Key.valueOf(keyAsString);
        } catch (IllegalArgumentException e) {
            final String errorMessage = ERROR_MESSAGE + ". getString for undefined key-string : " + keyAsString;
            _log.error(this, errorMessage);
            throw new AlarmMessageException(errorMessage);
        }
        return getString(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(final Key key) throws AlarmMessageException {
        String result = null;

        
        switch (key) {
            case EVENTTIME:
            	if (_anyData.getTimestamp() == null) {
                	result = "noTimeStamp";
                	return result; 
                }
            	SimpleDateFormat sdf = new SimpleDateFormat( JMS_DATE_FORMAT);
            	result = sdf.format(_anyData.getTimestamp().getMilliseconds());
                break;
            case NAME:
            	if (_anyData.getMetaData() == null) {
                	result = "noMetaData";
                	return result; 
                }
                result = _anyData.getMetaData().getName();
                break;
            case SEVERITY:
            	if (_anyData.getMetaData() == null) {
                	result = "noMetaData";
                	return result; 
                }
                result = getSeverityAsString(_anyData.getSeverity());
                break;
            case STATUS:
               /**
                * getStatus() is actually calling: getSeverity().toString()
                * toString was implemented in DynamicValueCondition as: 
                * 
				//		StringBuilder sb= new StringBuilder(256);
				//		sb.append(states.toString());
				//		if (timestamp!=null) {
				//			sb.append(", ");
				//			sb.append(timestamp);
				//		} else {
				//			sb.append(", no-time");
				//		}
				//		if (description!=null) {
				//			sb.append(", ");
				//			sb.append(description);
				//		}
				//		return sb.toString();
				 * 
				 * resulting message: STATUS=[ALARM], 2010-05-10T16:59:46.786747944, HIHI_ALARM
				 * 
				 * this was modified to actually create what we need:
				 * 
				//		if (description==null) {
				//			return "NO_STATUS";
				//		} else {
				//			return description;
				//		}
				 * 
				 * resulting message: STATUS=HIHI_ALARM
				 * 
                */
            	result = _anyData.getSeverity().descriptionToString();  
                break;
            case FACILITY:
                // TODO MCL: there's currently no facility available from DAL
            	// this could be retrieved from LDAP - necessary ?? 
            	result = "NN";
                break;
            case HOST:
                // TODO MCL: we can get the host name from the DAL connection - available?
            	if (_anyData.getMetaData() == null) {
                	result = "noMetaData";
                	return result; 
                }
            	String hostName = _anyData.getMetaData().getHostname();
            	if ( hostName != null) {
            		result = hostName;
            	} else {
            		result = "hostUndefined";
            	}
                break;
//            case TEXT:
//              // TODO MCL: this is actually the descriptor information from the channel
//            	// this is not available by default - it could be retrieved from the channel - as an additional DAL request
//            	// ... maybe too much effort ...
//                break;
            case TYPE:
                // TODO MCL: the type is an event-alarm from the IOC as a result ...
            	// we'll have to add this to the MAP manually - as a default value ...
            	result = "event";
                break;
            case VALUE:
                result = _anyData.stringValue();
                break;
            case APPLICATION_ID:
                result = Application_ID;
                break;
            default:
                _log.error(this, ERROR_MESSAGE + ". getString for undefined key : " + key);
//                throw new AlarmMessageException(ERROR_MESSAGE);
        }
        return result;
    }
    
    private String getSeverityAsString(final Severity severity) {
        String result = null;
        
        if (severity == null) {
        	return "noMetaData";
        } else if (severity.hasValue()) {
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
    
    @Override
    public Map<String, String> getMap() throws AlarmMessageException {
        // TODO jp performance: cache the result map.
        Map<String, String> result = new HashMap<String, String>();
        for (Key key : Key.values()) {
            result.put(key.name(), getString(key));
        }
        return result;
    }
    
    @Override
    public MapMessage getMapMessage(MapMessage message) throws AlarmMessageException, JMSException {
        // TODO jp performance: cache the result map.
        for (Key key : Key.values()) {
        	/*
        	 * if the value is noTimeStamp or Uninitialized or noMetaData
        	 * return null -> do NOT create message !
        	 */
        	String value = getString(key);
        	if ( value!=null && !value.equals("noTimeStamp")&& !value.equals("Uninitialized") && !value.equals("noMetaData")) {
        		message.setString(key.name(), value);
        	} else {
        		return null;
        	}
        }
        return message;
    }
}
