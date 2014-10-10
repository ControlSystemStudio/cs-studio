/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.pva;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.csstudio.vtype.pv.PV;
import org.epics.pvaccess.client.ChannelPut;
import org.epics.pvaccess.client.ChannelPutRequester;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Status;

/** A {@link ChannelPutRequester} for writing a value to a {@link PVA_PV},
 *  indicating completion via a {@link Future}
 * 
 *  @author Kay Kasemir
 */
class PVPutHandler extends PVRequester implements ChannelPutRequester, Future<Object>
{
    final private PV pv;
    final private Object new_value;

    final private CountDownLatch updates = new CountDownLatch(1);
    private volatile Exception error = null;

    private ChannelPut channelPut;

    /** @param pv PV to write
     *  @param new_value Value to write
     */
    public PVPutHandler(PV pv, Object new_value)
    {
        this.pv = pv;
        this.new_value = new_value;
    }

    // ChannelPutRequester
    @Override
    public void channelPutConnect(final Status status, final ChannelPut channelPut,
            final PVStructure pvStructure, final BitSet bitSet)
    {
        if (! status.isSuccess())
        {
            error = new Exception("Failed to connect 'put' for " + pv.getName() + ": " + status);
            updates.countDown();
            return;
        }
        
        try
        {
            // Locate the value field
            PVField field = pvStructure.getSubField("value");
            // It it enumerated? Write to index field
            if (field instanceof PVStructure  &&  "enum_t".equals(field.getField().getID()))
                field = ((PVStructure)field).getSubField("index");
            
            // Indicate what's changed & change it
            bitSet.set(field.getFieldOffset());
            PVStructureHelper.setField(field, new_value);
            
            // Perform write
            channelPut.put(true);
            
            this.channelPut = channelPut;
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Failed to write " + pv.getName() + " = " + new_value, ex);
            error = new Exception("Failed to write " + pv.getName() + " = " + new_value, ex);
            updates.countDown();
        }
    }

    // ChannelPutRequester
    @Override
    public void putDone(final Status status)
    {
        if (status.isSuccess())
            logger.log(Level.FINE, "Write {0} = {1} completed",
                    new Object[] { pv.getName(), new_value });
        else
        {
            error = new Exception("Write " + pv.getName() + " = " + new_value + " failed, " + status.toString());
            logger.log(Level.WARNING, "", error);
        }
        updates.countDown();
        channelPut.destroy();
    }

    // ChannelPutRequester
    @Override
    public void getDone(Status status)
    {
        // Only used for createChannelPutGet
    }

    // Future
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning)
    {
        return false;
    }

    // Future
    @Override
    public boolean isCancelled()
    {
        return false;
    }

    // Future
    @Override
    public boolean isDone()
    {
        return updates.getCount() == 0;
    }

    // Future
    @Override
    public Object get() throws InterruptedException, ExecutionException
    {
        updates.await();
        if (error != null)
            throw new ExecutionException(error);
        return null;
    }

    // Future
    @Override
    public Object get(final long timeout, final TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException
    {
        if (! updates.await(timeout, unit))
            throw new TimeoutException(pv.getName() + " write timeout");
        if (error != null)
            throw new ExecutionException(error);
        return null;
    }
}
