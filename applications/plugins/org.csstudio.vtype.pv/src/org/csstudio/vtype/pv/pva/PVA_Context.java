/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;
import java.util.logging.Logger;

import org.epics.pvaccess.ClientFactory;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.impl.remote.ClientContextImpl;

/** Singleton context for pvAccess
 *  @author Kay Kasemir
 */
public class PVA_Context
{
    private static PVA_Context instance;
    
    final private ClientContextImpl context;
    final private ChannelProvider provider;

    private PVA_Context() throws Exception
    {
        ClientFactory.start();
        context = new ClientContextImpl();
        context.initialize();
        
        Logger.getLogger(getClass().getName()).config(context.getVersion().toString());

        provider = context.getProvider();
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
        context.destroy();
        ClientFactory.stop();
    }
}
