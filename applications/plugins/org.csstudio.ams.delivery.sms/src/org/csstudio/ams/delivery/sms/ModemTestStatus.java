
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
 */

package org.csstudio.ams.delivery.sms;

import java.util.Vector;

/**
 * @author Markus Moeller
 *
 */
public class ModemTestStatus {

    private DeviceTestMessageContent initiatorMessage;

    /** Vector object that contains the names of all available modems */
    private final Vector<String> gateway;

    /** Vector object that contains the names of all modems that have caused an error */
    private final Vector<String> badModem;

    /** Content of property CLASS of the JMS message that causes the modem test */
    private String checkId;

    /** End time of the test */
    private long timeOut;

    /** Flag that indicates whether or not the current test is active */
    private boolean isActive;

    public static final String SMS_TEST_TEXT = "[MODEMTEST{$CHECKID,$GATEWAYID}]";

    public ModemTestStatus() {
        gateway = new Vector<String>();
        badModem = new Vector<String>();

        reset();
    }

    public void reset() {
        initiatorMessage = null;
        gateway.clear();
        badModem.clear();
        checkId = "";
        timeOut = 0;
        isActive = false;
    }

    public void checkAndRemove(final String content) {
        String text = null;

        for(final String name : gateway) {
            text = SMS_TEST_TEXT;
            text = text.replaceAll("\\$CHECKID", checkId);
            text = text.replaceAll("\\$GATEWAYID", name);

            if(content.compareTo(text) == 0)
            {
                gateway.removeElement(name);
                break;
            }
        }
    }

    public void moveGatewayIdToBadModems() {
        for(final String name : gateway) {
            if(badModem.contains(name) == false) {
                badModem.add(name);
            }
        }
    }

    public void setDeviceTestMessageContent(final DeviceTestMessageContent o) {
        initiatorMessage = o;
    }

    public DeviceTestMessageContent getDeviceTestMessageContent() {
        return initiatorMessage;
    }

    public boolean isDeviceTestInitiated() {
        return initiatorMessage != null;
    }

    public void finishedDeviceTestInitiated() {
        setDeviceTestMessageContent(null);
    }
    
    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(final String id) {
        this.checkId = id;
    }

    public boolean containsGatewayId(final String name) {
        return gateway.contains(name);
    }

    public void removeGatewayId(final String name) {
        if(gateway.contains(name)) {
            gateway.remove(name);
        }
    }

    public void addGatewayId(final String id) {
        this.gateway.add(id);
    }

    public int getGatewayCount() {
        return gateway.size();
    }

    public void addBadModem(final String name) {
        if(badModem.contains(name) == false) {
            badModem.add(name);
        }
    }

    public String[] getBadModems() {
        String[] result = new String[badModem.size()];
        result = badModem.toArray(result);
        return result;
    }

    public int getBadModemCount() {
        return badModem.size();
    }

    public void setTimeOut(final long timeout) {
        this.timeOut = timeout;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public boolean isTimeOut() {
        return System.currentTimeMillis() > timeOut;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return isActive;
    }
    /**
     * @param active the active to set
     */
    public void setActive(final boolean active) {
        this.isActive = active;
    }
}
