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
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: IAlarmMessage.java,v 1.3 2010/04/28
 * 07:44:08 jpenning Exp $
 */
package org.csstudio.alarm.service.declaration;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * Is used by the AlarmService to represent a message from DAL or JMS resp.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public interface IAlarmMessage {
    
    /**
     * Set of keys for the alarm message.
     * TODO: MCL support for '-' in enums like in APPLICATION-ID and change APPLICATION_ID
     */
    enum Key {
        EVENTTIME, NAME, SEVERITY, STATUS, FACILITY, HOST, TYPE, VALUE, APPLICATION_ID
    }
    
    /**
     * Set of keys for the alarm message - currently NOT supported
     * beware!!! SEVERITY_OLD translates into the TAG: SEVERITY-OLD !!!
     * the same applies for STATUS_OLD and HOST_PHYS
     */
    enum NoKey {
        ACK, SEVERITY_OLD, STATUS_OLD, HOST_PHYS, TEXT
    }
    
    /**
     * format of the time string
     */
    String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /**
     * application ID for this application
     */
    String Application_ID = "CSS_AlarmService";
    
    /**
     * The message essentially is a map from String to String. Here you get the value for the key.
     * 
     * @param key
     * @return value
     * @throws AlarmMessageException
     */
    String getString(@Nonnull final String key) throws AlarmMessageException;
    
    String getString(@Nonnull final Key key) throws AlarmMessageException;
    
    /**
     * The message essentially is a map from String to String. Here you get the whole map.
     * 
     * @return the map
     * @throws AlarmMessageException
     */
    Map<String, String> getMap() throws AlarmMessageException;

	MapMessage getMapMessage(MapMessage message) throws AlarmMessageException, JMSException;
}
