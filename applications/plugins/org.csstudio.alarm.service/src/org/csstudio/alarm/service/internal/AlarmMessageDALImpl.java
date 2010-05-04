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

import org.csstudio.alarm.service.declaration.AlarmMessageException;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.simple.AnyData;

/**
 * DAL based implementation of the message abstraction of the AlarmService
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmMessageDALImpl implements IAlarmMessage {
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
    public String getString(final String key) throws AlarmMessageException {
        String result = null;
        if (key.equals("ACK")) {
            result = "<ack omitted>"; // TODO jp
        } else if (key.equals("SEVERITY")) {
            result = _anyData.getSeverity().toString();
        } else if (key.equals("EVENTTIME")) {
            result = _anyData.getTimestamp().toString(); // TODO jp: ok?
        } else if (key.equals("NAME")) {
            result = _anyData.getMetaData().getName();
        } else {
            String errorMessage = "Error analyzing DAL message";
            _log.error(this, errorMessage);
            throw new AlarmMessageException(errorMessage);
        }
        return result;
    }
    
    @Override
    public Map<String, String> getMap() throws AlarmMessageException {
        // TODO jp performance: cache the result map. NYI
        Map<String, String> result = new HashMap<String, String>();
        result.put("ACK", getString("ACK"));
        result.put("SEVERITY", getString("SEVERITY"));
        result.put("EVENTTIME", getString("EVENTTIME"));
        result.put("NAME", getString("NAME"));
        return result;
    }
}
