
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.alarm.jms2ora.util;

import org.csstudio.alarm.jms2ora.VersionInfo;
import org.csstudio.domain.common.statistic.Collector;

/**
 * @author mmoeller
 * @version 1.0
 * @since 19.08.2011
 */
public class StatisticCollector {

    /**
     * Provides the number of received messages
     */
    private final Collector receivedMessages;

    /**
     *  Contains the number of filtered messages.
     *  Messages will be filtered if they have the same content
     *  and are delivered in a short time interval
     */
    private final Collector filteredMessages;

    /**
     *  Messages will be discard if one ore more properties matches some defined criteria
     *  (e.g. the property TYPE is test)
     */
    private final Collector discardedMessages;

    /**
     * Remembers the number of stored messages
     */
    private final Collector storedMessages;

    public StatisticCollector() {

        receivedMessages = new Collector();
        receivedMessages.setApplication(VersionInfo.NAME);
        receivedMessages.setDescriptor("Received messages");
        receivedMessages.setContinuousPrint(false);
        receivedMessages.setContinuousPrintCount(1000.0);

        filteredMessages = new Collector();
        filteredMessages.setApplication(VersionInfo.NAME);
        filteredMessages.setDescriptor("Filtered messages");
        filteredMessages.setContinuousPrint(false);
        filteredMessages.setContinuousPrintCount(1000.0);

        discardedMessages = new Collector();
        discardedMessages.setApplication(VersionInfo.NAME);
        discardedMessages.setDescriptor("Discarded messages");
        discardedMessages.setContinuousPrint(false);
        discardedMessages.setContinuousPrintCount(1000.0);

        storedMessages = new Collector();
        storedMessages.setApplication(VersionInfo.NAME);
        storedMessages.setDescriptor("Stored messages");
        storedMessages.setContinuousPrint(false);
        storedMessages.setContinuousPrintCount(1000.0);
    }

    public final Double getReceivedMessageCount() {
        return receivedMessages.getActualValue().getValue();
    }

    public void incrementReceivedMessages() {
        receivedMessages.incrementValue();
    }

    public final Double getFilteredMessagesCount() {
        return filteredMessages.getActualValue().getValue();
    }

    public void incrementFilteredMessages() {
        filteredMessages.incrementValue();
    }

    public final Double getDiscardedMessagesCount() {
        return discardedMessages.getActualValue().getValue();
    }

    public void incrementDiscardedMessages() {
        discardedMessages.incrementValue();
    }

    public final Double getStoredMessagesCount() {
        return storedMessages.getActualValue().getValue();
    }

    public void incrementStoredMessages() {
        storedMessages.incrementValue();
    }
    
    public void addStoredMessages(int value) {
        double current = storedMessages.getActualValue().getValue();
        storedMessages.setValue(value + current);
    }
}
