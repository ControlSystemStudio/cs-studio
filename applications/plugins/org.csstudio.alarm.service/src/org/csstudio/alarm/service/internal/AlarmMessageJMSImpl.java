/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 * 
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 * 
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.service.declaration.AlarmMessageException;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.logging.CentralLogger;

/**
 * JMS based implementation of the message abstraction of the AlarmService
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmMessageJMSImpl implements IAlarmMessage {
    private final CentralLogger _log = CentralLogger.getInstance();
    
    private final MapMessage _mapMessage;
    
    /**
     * Constructor.
     * 
     * @param mapMessage
     *            this message will be evaluated by subsequent calls to getString
     */
    public AlarmMessageJMSImpl(final MapMessage mapMessage) {
        this._mapMessage = mapMessage;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(final String key) throws AlarmMessageException {
        String result = null;
        try {
            result = _mapMessage.getString(key);
        } catch (JMSException e) {
            _log.error(this, "Error analyzing JMS message", e);
            throw new AlarmMessageException(e);
        }
        return result;
    }
    
    @Override
    public Map<String, String> getMap() throws AlarmMessageException {
        // TODO jp performance: cache the result map
        Map<String, String> result = new HashMap<String, String>();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<String> mapNames = _mapMessage.getMapNames();
            while (mapNames.hasMoreElements()) {
                String key = mapNames.nextElement();
                result.put(key.toUpperCase(), _mapMessage.getString(key));
            }
        } catch (JMSException e) {
            _log.error(this, "Error creating map from JMS message", e);
            throw new AlarmMessageException(e);
        }
        return result;
    }
}
