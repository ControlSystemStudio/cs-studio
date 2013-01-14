
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.deliverysystem.internal;

import org.csstudio.ams.application.deliverysystem.Activator;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 19.12.2011
 */
public class DeliverySystemPreference<T> extends AbstractPreference<T> {

    public static final DeliverySystemPreference<String> XMPP_SERVER =
            new DeliverySystemPreference<String>("xmppServer", "krynfs.desy.de");
    
    public static final DeliverySystemPreference<String> XMPP_USER =
            new DeliverySystemPreference<String>("xmppUser", "ams-delivery-system");

    public static final DeliverySystemPreference<String> XMPP_PASSWORD =
            new DeliverySystemPreference<String>("xmppPassword", "ams");

    public static final DeliverySystemPreference<String> DELIVERY_WORKER_LIST =
            new DeliverySystemPreference<String>("deliveryWorkerList", "");

    public static final DeliverySystemPreference<String> WORKER_STATUS_MAIL =
            new DeliverySystemPreference<String>("deliveryWorkerMail", "");

    public static final DeliverySystemPreference<Boolean> ENABLE_WORKER_STOP =
            new DeliverySystemPreference<Boolean>("enableWorkerStop", false);

    public static final DeliverySystemPreference<Boolean> ENABLE_WORKER_RESTART =
            new DeliverySystemPreference<Boolean>("enableWorkerRestart", false);

    public static final DeliverySystemPreference<Long> WORKER_STOP_TIMEOUT =
            new DeliverySystemPreference<Long>("workerStopTimeout", 6000L);

    private DeliverySystemPreference(final String keyAsString, final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) DeliverySystemPreference.class;
    }

    @Override
    public String getPluginID() {
        return Activator.PLUGIN_ID;
    }
}
