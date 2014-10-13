/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.client.CreateRequest;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvdata.monitor.Monitor;
import org.epics.pvdata.monitor.MonitorElement;
import org.epics.pvdata.monitor.MonitorRequester;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.Structure;
import org.epics.vtype.VEnum;
import org.epics.vtype.VType;

/** pvAccess {@link PV}
 * 
 *  <p>Based on ideas from msekoranja org.epics.pvmanager.pva.PVAChannelHandler
 *  @author Kay Kasemir
 */
class PVA_PV extends PV implements ChannelRequester, MonitorRequester
{
    final private static Logger logger = Logger.getLogger(PVA_PV.class.getName());

    final private static short priority = ChannelProvider.PRIORITY_DEFAULT;

    /** Request used for reading all fields */
    final private static PVStructure read_request = CreateRequest.create().createRequest("field()");

    /** Request used for writing 'value' field */
    final private static PVStructure write_request = CreateRequest.create().createRequest("field(value)");

    /** PVAccess channel, also holds the 'base_name' */
    final private Channel channel;

    /** If channel is enumerated, this holds its most recent labels */
    private volatile List<String> enum_labels = null;
    
    private Monitor value_monitor = null;
    
    /** Initialize
     *  @param name Full name, may include "pva://"
     *  @param base_name Base name without optional prefix
     *  @param provider {@link ChannelProvider}
     *  @throws Exception on error
     */
    PVA_PV(final String name, final String base_name) throws Exception
    {
        super(name);
        channel = PVA_Context.getInstance().getProvider()
                             .createChannel(base_name, this, priority);
    }

    // ChannelRequester
    @Override
    public String getRequesterName()
    {
        return getClass().getName();
    }

    // ChannelRequester
    @Override
    public void message(final String message, final MessageType type)
    {
        switch (type)
        {
        case fatalError:
            logger.log(Level.SEVERE, message);
            break;
        case error:
        case warning:
            logger.log(Level.WARNING, message);
            break;
        default:
            logger.log(Level.INFO, message);
        }
    }

    // ChannelRequester
    @Override
    public void channelCreated(final Status status, final Channel channel)
    {
        if (status.isSuccess())
            logger.log(Level.FINE, "Channel {0} created", channel.getChannelName());
        else
            logger.log(Level.WARNING, "Channel {0} status {1}",
                                   new Object[] { channel.getChannelName(), status.getMessage() });
    }

    // ChannelRequester
    @Override
    public void channelStateChange(final Channel channel, final ConnectionState state)
    {
        logger.log(Level.FINE, "Channel {0} {1}",
                new Object[] { channel.getChannelName(), state });
        switch (state)
        {
        case CONNECTED:
            subscribe();
            break;
        case DISCONNECTED:
            notifyListenersOfDisconnect();
        default:
            // Ignore
        }
    }
    
    private void subscribe()
    {
        synchronized (this)
        {   // Avoid double-subscription
            if (this.value_monitor != null)
                return;
            value_monitor = channel.createMonitor(this, read_request);
        }
    }

    // MonitorRequester
    @Override
    public void monitorConnect(final Status status, final Monitor monitor,
            final Structure structure)
    {
        if (status.isSuccess())
            monitor.start();
    }

    VType handleValueUpdate(final PVStructure update) throws Exception
    {
        // TODO Copy only changes?
        // System.out.println(update.getChangedBitSet());
        // System.out.println(struct);

        final VType value = PVStructureHelper.getVType(update);
        if (value instanceof VEnum)
        {   // Remember most recent labels, but note that
            // not all updates will include the complete labels?!
            final List<String> labels = ((VEnum)value).getLabels();
            if (! labels.isEmpty())
                enum_labels = labels;
        }
        notifyListenersOfValue(value);
        return value;
    }
    
    // MonitorRequester
    @Override
    public void monitorEvent(final Monitor monitor)
    {
        MonitorElement update;
        while ((update = monitor.poll()) != null)
        {
            try
            {
                handleValueUpdate(update.getPVStructure());
            }
            catch (Exception ex)
            {
                logger.log(Level.WARNING,
                    "Cannot handle update for " + channel.getChannelName(), ex);
            }
            monitor.release(update);
        }            
    }

    // MonitorRequester
    @Override
    public void unlisten(final Monitor monitor)
    {
        // Ignore            
    }
    
    /** {@inheritDoc} */
    @Override
    public Future<VType> asyncRead() throws Exception
    {
        final PVGetHandler result = new PVGetHandler(this);
        channel.createChannelGet(result, read_request);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void write(final Object new_value) throws Exception
    {   // Submit write, but don't await/check its completion
        asyncWrite(new_value);
    }
    
    /** {@inheritDoc} */
    @Override
    public Future<?> asyncWrite(Object new_value) throws Exception
    {
        if (enum_labels != null  &&  new_value instanceof String)
        {   // Convert string-for-enum into index of corresponding label
            final VType current = read();
            if (current instanceof VEnum)
            {
                final int index = enum_labels.indexOf(new_value);
                if (index < 0)
                    throw new Exception("Cannot obtain label's index for enum PV " + getName());
                new_value = index;
            }
        }
        
        final PVPutHandler requester = new PVPutHandler(this, new_value);
        channel.createChannelPut(requester, write_request);
        return requester;
    }

    /** {@inheritDoc} */
    @Override
    protected void close()
    {
        channel.destroy();
    }
}