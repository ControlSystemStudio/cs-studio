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
 */
package org.csstudio.domain.common.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.util.SubnetUtils;
import org.csstudio.domain.common.preferences.ControlSubnetPreference;

/**
 * @author hrickens
 * @since 22.12.2011
 */
public final class GatewayUtil {
    public static boolean hasGateway() {
        boolean inSubnet = false;
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            final String myAddress = localHost.getHostAddress();
            final List<SubnetUtils> controlSubnets = getControlSubnets();
            for (final SubnetUtils subnetUtils : controlSubnets) {
                inSubnet |= subnetUtils.getInfo().isInRange(myAddress);
            }
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        } finally {
        }
        return !inSubnet;
    }

    public static List<SubnetUtils> getControlSubnets() {
        final ArrayList<SubnetUtils> controlSubnetsList = new ArrayList<SubnetUtils>();
        final List<String> controlSubnets = ControlSubnetPreference.getControlSubnets();
        for (final String string : controlSubnets) {
            final String[] split = string.split("/");
            if (split.length == 2) {
                controlSubnetsList.add(new SubnetUtils(split[0], split[1]));
            }
        }
        return controlSubnetsList;
    }
}
