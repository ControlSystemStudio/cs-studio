/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.device;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVListenerAdapter;
import org.csstudio.vtype.pv.PVPool;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/** {@link Device} that is connected to a Process Variable,
 *  supporting read and write access to that PV.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDevice extends Device
{
    /** 'compile time' option to treat byte arrays as string */
    final private static boolean TREAD_BYTES_AS_STRING = true; // TODO Make configurable
    
    /** Alarm that is used to identify a disconnected PV */
   final private static Alarm DISCONNECTED = ValueFactory.newAlarm(AlarmSeverity.INVALID, "Disconnected");
    
	/** Is the underlying PV type a BYTE[]?
	 *  @see #TREAD_BYTES_AS_STRING
	 */
	private boolean is_byte_array = false;
	
	/** Most recent value of the PV
	 *  SYNC on this
	 */
	private VType value = getDisconnectedValue();

	/** Underlying control system PV
	 *  SYNC on this
	 */
	private PV pv;
	
	final private PVListener pv_listener = new PVListenerAdapter()
    {
        @Override
        public void valueChanged(final PV pv, final VType new_value)
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE,
                "PV {0} received {1}", new Object[] { getName(), new_value });
            synchronized (PVDevice.this)
            {
                value = wrapReceivedValue(new_value);
            }
            fireDeviceUpdate();
        }
        
        @Override
        public void disconnected(PV pv)
        {
            value = getDisconnectedValue();
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "PV " + getName() + " disconnected");
            fireDeviceUpdate();
        }
    };

    private VType wrapReceivedValue(VType new_value)
    {
        if (new_value == null)
            return getDisconnectedValue();
        else if (TREAD_BYTES_AS_STRING  && new_value instanceof VByteArray)
        {
            is_byte_array = true;
            final VByteArray barray = (VByteArray) new_value;
            new_value = ValueFactory.newVString(
                    ByteHelper.toString(barray), (Alarm)barray, (Time)barray);
            Logger.getLogger(getClass().getName()).log(Level.FINE,
                    "PV BYTE[] converted to {0}", new_value);
            return new_value;
        }
        else
            return new_value;

    }
    
	/** Initialize
	 *  @param info {@link DeviceInfo}
	 *  @throws Exception on error during PV setup
	 */
	public PVDevice(final DeviceInfo info) throws Exception
    {
	    super(info);
    }
	
	/** {@inheritDoc} */
	@Override
    public void start() throws Exception
	{
		synchronized (this)
		{
            pv = PVPool.getPV(getName());
		}
		pv.addListener(pv_listener);
	}

	/** {@inheritDoc} */
    @Override
    public synchronized boolean isReady()
    {
        if (pv == null  ||  value == null)
            return false;
        // A value might _implement_ Alarm to represent DISCONNECTED,
        // but be a different object,
        // so there is no quick way to check alarm == DISCONNECTED.
        final Alarm alarm = ValueUtil.alarmOf(value);
        return alarm.getAlarmSeverity() != DISCONNECTED.getAlarmSeverity()  ||
               ! alarm.getAlarmName().equals(DISCONNECTED.getAlarmName());
    }

	/** @return Human-readable device status */
    @Override
    public synchronized String getStatus()
    {
        if (pv == null)
            return "no PV";
        else
            return VTypeHelper.toString(value);
    }

    /** {@inheritDoc} */
	@Override
    public void stop()
	{
		final PV copy;
		synchronized (this)
		{
			copy = pv;
		}
		copy.removeListener(pv_listener);
		PVPool.releasePV(copy);
		synchronized (this)
		{
			pv = null;
			value = getDisconnectedValue();
		}
	}

	/** {@inheritDoc} */
	@Override
    public VType read() throws Exception
    {
		final VType current;
		synchronized (this)
        {
            current = this.value;
        }
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Reading: PV {0} = {1}",
				new Object[] { getName(), current });
		return current;
    }
	
	/** Turn {@link TimeDuration} into millisecs for {@link TimeUnit} API
	 *  @param timeout {@link TimeDuration}
	 *  @return Milliseconds or 0
	 */
	private static long getMillisecs(final TimeDuration timeout)
	{
	    if (timeout == null  ||  ! timeout.isPositive())
	        return 0;
	    return timeout.getSec() * 1000L  +  timeout.getNanoSec() / 1000;
	}
	
	/** {@inheritDoc} */
    @Override
    public VType read(final TimeDuration timeout) throws Exception
    {        
        final PV pv; // Copy to access PV outside of lock
        synchronized (this)
        {
            pv = this.pv;
        }
        try
        {
            final Future<VType> read_result = pv.asyncRead();
            final long millisec = getMillisecs(timeout);
            final VType received_value = (millisec > 0)
                ? read_result.get(millisec, TimeUnit.MILLISECONDS)
                : read_result.get();
            synchronized (this)
            {
                value = wrapReceivedValue(received_value);;
                return value;
            }
        }
        catch (Exception ex)
        {
            synchronized (this)
            {
                value = getDisconnectedValue();
            }
            throw ex;
        }
    }	
	
    /** @return 'Disconnected' Value with current time stamp */
    final private static VType getDisconnectedValue()
    {
        return ValueFactory.newVString(DISCONNECTED.getAlarmName(), DISCONNECTED, ValueFactory.timeNow());
    }

	/** Write value to device, with special handling of EPICS BYTE[] as String 
     *  @param value Value to write (Double, String)
     *  @throws Exception on error: Cannot write, ...
     */
    @Override
    public void write(Object value) throws Exception
    {
        if (is_byte_array  &&  value instanceof String)
            value = ByteHelper.toBytes((String) value);

        Logger.getLogger(getClass().getName()).log(Level.FINER, "Writing: PV {0} = {1}",
                new Object[] { getName(), value });
        final PV pv; // Copy to access PV outside of lock
        synchronized (this)
        {
            pv = this.pv;
        }
        pv.write(value);
    }
	
	/** Write value to device, with special handling of EPICS BYTE[] as String 
     *  @param value Value to write (Double, String)
     *  @param timeout Timeout, <code>null</code> as "forever"
     *  @throws Exception on error: Cannot write, ...
     */
	@Override
    public void write(Object value, final TimeDuration timeout) throws Exception
    {
	    if (is_byte_array  &&  value instanceof String)
	        value = ByteHelper.toBytes((String) value);

	    Logger.getLogger(getClass().getName()).log(Level.FINE, "Writing with completion: PV {0} = {1}",
	            new Object[] { getName(), value });

	    final PV pv; // Copy to access PV outside of lock
	    synchronized (this)
		{
	        pv = this.pv;
		}
	    final Future<?> write_result = pv.asyncWrite(value);
	    final long millisec = getMillisecs(timeout);
	    if (millisec > 0)
	        write_result.get(millisec, TimeUnit.MILLISECONDS);
	    else
	        write_result.get();
    }
}
