
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

import java.util.Hashtable;
import org.csstudio.platform.statistic.Collector;
import org.smslib.AGateway;
import org.smslib.IGatewayStatusNotification;
import org.smslib.AGateway.GatewayStatuses;

/**
 * @author Markus Moeller
 *
 */
public class GatewayStatusNotification implements IGatewayStatusNotification
{
    /** Contains the Collector objects that hold the restart count for each modem */
    private final Hashtable<String, Collector> gatewayRestart;

    public GatewayStatusNotification(final String[] gatewayId)
    {
        gatewayRestart = new Hashtable<String, Collector>();

        if(gatewayId != null)
        {
            for(final String s : gatewayId)
            {
                if(s != null)
                {
                    final Collector c = new Collector();
                    c.setApplication("AmsSmsConnector");
                    c.setDescriptor("Restart count of " + s);
                    c.setContinuousPrint(false);
                    c.setContinuousPrintCount(1000.0);
                    gatewayRestart.put(s, c);
                }
            }
        }
    }

    @Override
    public void process(final AGateway gateway, final GatewayStatuses oldStatus, final GatewayStatuses newStatus) {
        if((gatewayRestart.containsKey(gateway.getGatewayId())) && (newStatus == GatewayStatuses.RESTART))
        {
            gatewayRestart.get(gateway.getGatewayId()).incrementValue();
        }
    }
}
