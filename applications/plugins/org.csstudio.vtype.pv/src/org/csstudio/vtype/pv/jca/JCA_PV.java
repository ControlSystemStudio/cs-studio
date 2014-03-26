/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.jca;

import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.AccessRightsEvent;
import gov.aps.jca.event.AccessRightsListener;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.internal.Preferences;
import org.epics.vtype.VType;

/** Channel Access {@link PV}
 *  @author Kay Kasemir
 */
public class JCA_PV extends PV implements ConnectionListener, MonitorListener, AccessRightsListener
{
    final private static Logger logger = Logger.getLogger(JCA_PV.class.getName());

    /** Request plain DBR type or ..TIME..? */
    final private boolean plain_dbr;
    
    /** JCA Channel */
    final private Channel channel;
    
    private volatile Object metadata;
    
    final private GetListener meta_get_listener = new GetListener()
    {
        @Override
        public void getCompleted(final GetEvent ev)
        {
            if (! ev.getStatus().isSuccessful())
                JCA_PV.this.notifyListenersOfDisconnect();
            else
            {
                metadata = ev.getDBR();
                logger.fine(getName() + " received meta data");
                subscribe();
            }
        }
    };

    private Monitor value_monitor;

    /** Initialize
     *  @param name Full name, may include "ca://"
     *  @param base_name Base name without optional prefix
     *  @throws Exception on error
     */
    public JCA_PV(final String name, final String base_name) throws Exception
    {
        super(name);
        logger.fine("JCA PV " + base_name);
        // .RTYP does not provide meta data
        plain_dbr = base_name.endsWith(".RTYP");
        channel = JCAContext.getInstance().getContext().createChannel(base_name, this);
        channel.getContext().flushIO();
    }
    
    /** JCA connection listener */
    @Override
    public void connectionChanged(final ConnectionEvent ev)
    {
        if (ev.isConnected())
        {
            logger.fine(getName() + " connected");
            getMetaData(); // .. and start subscription
        }
        else
        {
            logger.fine(getName() + " disconnected");
            notifyListenersOfDisconnect();
            // On re-connect, fetch meta data
            // and start subscription (possibly for changed type after IOC reboot)
            unsubscribe();
        }
    }

    private void getMetaData()
    {
        try
        {
            channel.get(DBRHelper.getCtrlType(plain_dbr, channel.getFieldType()), 1, meta_get_listener);
            channel.getContext().flushIO();
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, getName() + " cannot get meta data", ex);
        }
    }
    
    private void subscribe()
    {
        synchronized (this)
        {   // Avoid double-subscription
            if (this.value_monitor != null)
                return;
        }
        
        try
        {
            logger.log(Level.FINE, getName() + " subscribes");
            final int mask = Preferences.monitorMask().getMask();
            final Monitor value_monitor = channel.addMonitor(DBRHelper.getTimeType(plain_dbr, channel.getFieldType()), channel.getElementCount(), mask, this);
            
            synchronized (this)
            {   // Not holding the lock; could have been another subscription while we established this one...
                if (this.value_monitor != null)
                {
                    logger.log(Level.FINE, getName() + " already had a subscribtion");
                    this.value_monitor.clear();
                }
                this.value_monitor = value_monitor;
            }
            // TODO Monitor.PROPERTY subscription

            channel.addAccessRightsListener(this);
            channel.getContext().flushIO();
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, getName() + " cannot subscribe", ex);
        }
    }
    
    private void unsubscribe()
    {
        final Monitor value_monitor;
        synchronized (this)
        {
            value_monitor = this.value_monitor;
            this.value_monitor = null;
        }
        if (value_monitor == null)
            return;
        logger.log(Level.FINE, getName() + " unsubscribes");
        try
        {
            channel.removeAccessRightsListener(this);
            value_monitor.clear();
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, getName() + " cannot subscribe", ex);
        }
    }

    @Override
    public void accessRightsChanged(final AccessRightsEvent ev)
    {
        final boolean readonly = ! ev.getWriteAccess();
        logger.fine(getName() + (readonly ? " is read-only" : " is writeable"));
        notifyListenersOfPermissions(readonly);
    }
    
    @Override
    public void monitorChanged(final MonitorEvent ev)
    {
        try
        {   // May receive event with null status when 'disconnected'
            final CAStatus status = ev.getStatus();
            if (status != null  &&  status.isSuccessful())
            {
                final VType value = DBRHelper.decodeValue(metadata, ev.getDBR());
                logger.log(Level.FINE, "{0} = {1}", new Object[] { getName(), value });
                notifyListenersOfValue(value);
            }
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, getName() + " monitor error", ex);
            ex.printStackTrace();
        }
    }
    
    /** {@link Future} that acts as JCA {@link GetListener}
     *  and provides the value or error to user of the {@link Future}
     */
    private class GetCallbackFuture implements Future<VType>, GetListener
    {
        final private CountDownLatch updates = new CountDownLatch(1);
        private volatile VType value;
        private volatile Exception error;
                
        @Override
        public void getCompleted(final GetEvent ev)
        {
            try
            {
                if (ev.getStatus().isSuccessful())
                {
                    value = DBRHelper.decodeValue(metadata, ev.getDBR());
                    logger.log(Level.FINE, "{0} get-callback {1}", new Object[] { getName(), value });
                    notifyListenersOfValue(value);
                }
                else
                {
                    notifyListenersOfDisconnect();
                    error = new Exception(ev.getStatus().getMessage());
                }
            }
            catch (Exception ex)
            {
                error = ex;
            }
            updates.countDown();
        }        

        @Override
        public boolean cancel(boolean mayInterruptIfRunning)
        {
            return false;
        }

        @Override
        public boolean isCancelled()
        {
            return false;
        }

        @Override
        public boolean isDone()
        {
            return updates.getCount() == 0;
        }

        @Override
        public VType get() throws InterruptedException, ExecutionException
        {
            updates.await();
            if (error != null)
                throw new ExecutionException(error);
            return value;
        }

        @Override
        public VType get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException,
                TimeoutException
        {
            if (! updates.await(timeout, unit))
                throw new TimeoutException(getName() + " read timeout");
            if (error != null)
                throw new ExecutionException(error);
            return value;
        }
    }
   
    @Override
    public Future<VType> asyncRead() throws Exception
    {
        final DBRType type = channel.getFieldType();
        if (type == null   ||  type == DBRType.UNKNOWN)
                throw new Exception(getName() + " is not connected");
        final GetCallbackFuture result = new GetCallbackFuture();
        channel.get(DBRHelper.getTimeType(plain_dbr, type), channel.getElementCount(), result);
        channel.getContext().flushIO();
        return result;
    }

    /** {@link Future} that acts as JCA {@link PutListener}
     *  and provides error to user of the {@link Future}
     */
    private class PutCallbackFuture implements Future<VType>, PutListener
    {
        final private CountDownLatch updates = new CountDownLatch(1);
        private volatile Exception error;

        @Override
        public void putCompleted(final PutEvent ev)
        {
            if (! ev.getStatus().isSuccessful())
                error = new Exception(getName() + " write failed: " + ev.getStatus().getMessage());
            updates.countDown();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning)
        {
            return false;
        }

        @Override
        public boolean isCancelled()
        {
            return false;
        }

        @Override
        public boolean isDone()
        {
            return updates.getCount() == 0;
        }

        @Override
        public VType get() throws InterruptedException, ExecutionException
        {
            updates.await();
            if (error != null)
                throw new ExecutionException(error);
            return null;
        }

        @Override
        public VType get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException,
                TimeoutException
        {
            if (! updates.await(timeout, unit))
                throw new TimeoutException(getName() + " write timeout");
            if (error != null)
                throw new ExecutionException(error);
            return null;
        }
    }

    @Override
    public void write(final Object new_value) throws Exception
    {
        performWrite(new_value, null);
    }

    @Override
    public Future<?> asyncWrite(final Object new_value) throws Exception
    {
        final PutCallbackFuture result = new PutCallbackFuture();
        performWrite(new_value, result);
        return result;
    }
    
    private void performWrite(final Object new_value, final PutListener put_listener) throws Exception
    {
        if (new_value instanceof String)
        {
            if (channel.getFieldType().isBYTE()  &&  channel.getElementCount() > 1)
            {   // Long string support: Write characters of string as DBF_CHAR array
                final char[] chars = ((String) new_value).toCharArray();
                final int[] codes = new int[chars.length+1];
                for (int i=0; i<chars.length; ++i)
                    codes[i] = chars[i];
                codes[chars.length] = 0;
                if (put_listener != null)
                    channel.put(codes, put_listener);
                else
                    channel.put(codes);
            }
            else
            {
                if (put_listener != null)
                    channel.put((String)new_value, put_listener);
                else
                    channel.put((String)new_value);
            }
        }
        else if (new_value instanceof Double)
        {
            final double val = ((Double)new_value).doubleValue();
            if (put_listener != null)
                channel.put(val, put_listener);
            else
                channel.put(val);
        }
        else if (new_value instanceof Double [])
        {
            final Double dbl[] = (Double [])new_value;
            final double val[] = new double[dbl.length];
            for (int i=0; i<val.length; ++i)
                val[i] = dbl[i].doubleValue();
            if (put_listener != null)
                channel.put(val, put_listener);
            else
                channel.put(val);
        }
        else if (new_value instanceof Integer)
        {
            final int val = ((Integer)new_value).intValue();
            if (put_listener != null)
                channel.put(val, put_listener);
            else
                channel.put(val);
        }
        else if (new_value instanceof Integer [])
        {
            final Integer ival[] = (Integer [])new_value;
            final int val[] = new int[ival.length];
            for (int i=0; i<val.length; ++i)
                val[i] = ival[i].intValue();
            if (put_listener != null)
                channel.put(val, put_listener);
            else
                channel.put(val);
        }
        else if (new_value instanceof int[])
        {
            if (put_listener != null)
                channel.put((int[])new_value, put_listener);
            else
                channel.put((int[])new_value);
        }
        else if (new_value instanceof double[])
        {
            if (put_listener != null)
                channel.put((double[])new_value, put_listener);
            else
                channel.put((double[])new_value);
        }
        else if (new_value instanceof byte[])
        {
            if (put_listener != null)
                channel.put((byte[])new_value, put_listener);
            else
                channel.put((byte[])new_value);
        }
        else if (new_value instanceof short[])
        {
            if (put_listener != null)
                channel.put((short[])new_value, put_listener);
            else
                channel.put((short[])new_value);
        }
        else if (new_value instanceof float[])
        {
            if (put_listener != null)
                channel.put((float[])new_value, put_listener);
            else
                channel.put((float[])new_value);
        }
        else
            throw new Exception("Cannot handle type "
                                    + new_value.getClass().getName());
        // When performing many consecutive writes,
        // sending them in 'bulk' would be more efficient,
        // but in most case it's probably better to perform each write ASAP
        channel.getContext().flushIO();
    }

    /** {@inheritDoc} */
    @Override
    protected void close()
    {
        channel.dispose();
    }
}
