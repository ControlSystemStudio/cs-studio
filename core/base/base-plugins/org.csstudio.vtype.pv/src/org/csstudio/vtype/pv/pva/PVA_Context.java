/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import static org.csstudio.vtype.pv.PV.logger;

import java.util.Arrays;
import java.util.logging.Level;

import org.epics.pvaccess.ClientFactory;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelProviderRegistry;
import org.epics.pvaccess.client.ChannelProviderRegistryFactory;

/** Singleton context for pvAccess
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVA_Context
{
    private static PVA_Context instance;

    final private ChannelProvider provider;

    private PVA_Context() throws Exception
    {
        ClientFactory.start();
        final ChannelProviderRegistry registry = ChannelProviderRegistryFactory.getChannelProviderRegistry();
        provider = registry.getProvider("pva");
        if (provider == null)
            throw new Exception("Tried to locate 'pva' provider, found " + Arrays.toString(registry.getProviderNames()));
        logger.log(Level.CONFIG, "PVA Provider {0}", provider.getProviderName());
    }

    /** @return Singleton instance */
    public static synchronized PVA_Context getInstance() throws Exception
    {
        if (instance == null)
            instance = new PVA_Context();
        return instance;
    }

    /** @return {@link ChannelProvider} */
    public ChannelProvider getProvider()
    {
        return provider;
    }

    /** In tests, the context can be closed to check cleanup,
     *  but operationally the singleton will remain open.
     */
    public void close()
    {
        ClientFactory.stop();
    }
}
