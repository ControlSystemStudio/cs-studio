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
            _log.error(this, ERROR_MESSAGE);
            throw new AlarmMessageException(ERROR_MESSAGE);
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
            case ACK:
                result = "<ack omitted>"; // TODO jp
                break;
            case EVENTTIME:
                result = _anyData.getTimestamp().toString(); // TODO jp: ok?
                break;
            case NAME:
                result = _anyData.getMetaData().getName();
                break;
            case SEVERITY:
                result = getSeverityAsString(_anyData.getSeverity());
                break;
            case STATUS:
                result = getStatusAsString(_anyData.getStatus());
                break;
            default:
                _log.error(this, ERROR_MESSAGE);
                throw new AlarmMessageException(ERROR_MESSAGE);
        }
        return result;
    }
    
    private String getStatusAsString(final String status) {
        // TODO jp NYI
        return status;
    }
    
    private String getSeverityAsString(final Severity severity) {
        String result = null;
        
        if (severity.hasValue()) {
            result = "UNDEFINED";
        } else if (severity.isMajor()) {
            result = "MAJOR";
        } else if (severity.isMinor()) {
            result = "MAJOR";
        } else if (severity.isOK()) {
            result = "NO ALARM";
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
}
