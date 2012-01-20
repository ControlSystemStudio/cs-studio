/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.cagateway.preferences;


import javax.annotation.Nonnull;

import org.csstudio.cagateway.Activator;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * Constant definitions for plug-in preferences
 *
 *
 * @author jpenning
 * @author $Author: claus $
 * @version $Revision: 1.4 $
 * @since 10.06.2010

 * @param <T> the type of the preference. It must match the type of the default value.
 */
public final class CAGatewayPreference<T> extends AbstractPreference<T> {

    public static final CAGatewayPreference<String> XMPP_SERVER_NAME =
        new CAGatewayPreference<String>("XmppServerName", "krynfs.desy.de");
    public static final CAGatewayPreference<String> XMPP_USER_NAME =
        new CAGatewayPreference<String>("XmppUserName", "cagateway");
    public static final CAGatewayPreference<String> XMPP_PASSWORD =
        new CAGatewayPreference<String>("XmppPassword", "cagateway");
    
	public static final CAGatewayPreference<Integer> JMS_TIME_TO_LIVE_ALARMS = 
		new CAGatewayPreference<Integer> ("jmsTimeToLiveAlarms",3600000);
	public static final CAGatewayPreference<Integer> JMS_TIME_TO_LIVE_LOGS = 
		new CAGatewayPreference<Integer> ("jmsTimeToLiveLogs", 600000);
	public static final CAGatewayPreference<Integer> JMS_TIME_TO_LIVE_PUT_LOGS = 
		new CAGatewayPreference<Integer> ("jmsTimeToLivePutLogs", 3600000);


    private CAGatewayPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginID() {
        return Activator.PLUGIN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) CAGatewayPreference.class;
    }

}
