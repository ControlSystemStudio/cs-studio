/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import org.epics.pvaccess.CAException;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.impl.remote.ClientContextImpl;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.vtype.DataTypeSupport;

/**
 * 
 * @author msekoranja
 */
public class PVADataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    //private static final Logger logger = Logger.getLogger(PVADataSource.class.getName());

    private final short defaultPriority;
    private final ChannelProvider pvaChannelProvider;
    
    // this grabs internal implementation (and does not get ChannelProvider via ChannelAccess)
    // to allow clean shutdown (no such API for now)
    private final ClientContextImpl pvaContext;
    
    private final PVATypeSupport pvaTypeSupport = new PVATypeSupport(new PVAVTypeAdapterSet());
    
    public PVADataSource() {
    	this(ChannelProvider.PRIORITY_DEFAULT);  	
    }
    
    public PVADataSource(short defaultPriority) {
    	super(true);
        this.pvaContext = new ClientContextImpl();
        this.pvaChannelProvider = pvaContext.getProvider();
        this.defaultPriority = defaultPriority;
        
        // force initialization now
        try {
			pvaContext.initialize();
		} catch (CAException e) {
			throw new RuntimeException("Failed to intialize pvAccess context.", e);
		}
    }

    public PVADataSource(ChannelProvider channelProvider, short defaultPriority) {
        super(true);
        this.pvaContext = null;
        this.pvaChannelProvider = channelProvider;
        this.defaultPriority = defaultPriority;
    }

    public short getDefaultPriority() {
        return defaultPriority;
    }
    
    public void close() {
    	// TODO destroy via ChannelProvider when API supports it
    	if (pvaContext != null)
    		pvaContext.dispose();
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        return new PVAChannelHandler(channelName, pvaChannelProvider, defaultPriority, pvaTypeSupport);
    }

}
