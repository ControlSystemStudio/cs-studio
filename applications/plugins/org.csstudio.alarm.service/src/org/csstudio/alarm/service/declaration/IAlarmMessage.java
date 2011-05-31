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

import java.util.Date;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;

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
     * The message essentially is a map from String to String. Here you get the value for the key (given as enum).
     * The method getDefiningName() of the key determines the string for the key.
     *
     * @param key
     * @return value
     */
    @Nonnull
    String getString(@Nonnull final AlarmMessageKey key);

    /**
     * @return the severity
     */
    @Nonnull
    EpicsAlarmSeverity getSeverity();

    /**
     * Tries to retrieve the event time and returns the date if it succeeds, else null.
     *
     * @return the event time or null
     */
    @CheckForNull
    Date getEventtime();

    /**
     * Tries to retrieve the event time and returns the date if it succeeds, else the current time.
     *
     * @return the event time or the current time
     */
    @Nonnull
    Date getEventtimeOrCurrentTime();

    /**
     * @return true if this message is an alarm acknowledgement.
     */
    boolean isAcknowledgement();

    /**
     * The message essentially is a map from String to String. Here you get the whole map with
     * the following content:
     * map: Key.getDefiningName() -> Value
     *
     * @return the map
     */
    @Nonnull
    Map<String, String> getMap();

}
