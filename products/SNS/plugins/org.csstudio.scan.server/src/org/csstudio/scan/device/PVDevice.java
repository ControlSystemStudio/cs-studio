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

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/** {@link Device} that is connected to a Process Variable,
 *  supporting read and write access to that PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDevice extends Device
{
    /** PVManager uses this PV name annotation to enable put-callback */
    final public static String PUT_CALLBACK_ANNOTATION = " {\"putCallback\":true}";

    /** 'compile time' option to treat byte arrays as string */
    final private static boolean TREAD_BYTES_AS_STRING = true;
    
	/** Value that is used to identify a disconnected PV */
	final private static VType DISCONNECTED =
			ValueFactory.newVString("Disconnected", ValueFactory.newAlarm(AlarmSeverity.INVALID, "Disconnected"), ValueFactory.timeNow());
	
	/** Is the underlying PV type a BYTE[]?
	 *  @see #TREAD_BYTES_AS_STRING
	 */
	private boolean is_byte_array = false;
	
	/** Is PV using a put-callback, i.e. wait in write() until
	 *  the write confirmation is received?
	 */
	final private boolean use_put_callback;
	
	/** Flag set when callback is received */
	private boolean received_put_callback;
	
	/** Most recent value of the PV
	 *  SYNC on this
	 */
	private VType value = DISCONNECTED;

	/** Underlying control system PV
	 *  SYNC on this
	 */
	private PV<VType, Object> pv;
	
	/** Initialize
	 *  @param info {@link DeviceInfo}
	 *  @throws Exception on error during PV setup
	 */
	public PVDevice(final DeviceInfo info) throws Exception
    {
	    super(info);
	    use_put_callback = info.getName().endsWith(PUT_CALLBACK_ANNOTATION);
    }
	
	/** {@inheritDoc} */
	@Override
    public void start() throws Exception
	{
		final PVReaderListener<VType> listener = new PVReaderListener<VType>()
		{
			@Override
			public void pvChanged(final PVReaderEvent<VType> event)
			{
				final PVReader<VType> pv = event.getPvReader();
				final Exception error = pv.lastException();
					
				synchronized (PVDevice.this)
				{					
					if (error != null)
					{
						value = DISCONNECTED;
                        Logger.getLogger(getClass().getName()).log(Level.WARNING,
                            "PV " + getName() + " error",
                            error);
					}
					else
					{
						value = pv.getValue();
						final Alarm alarm = ValueUtil.alarmOf(value);
						if (!pv.isConnected()  ||
						    (alarm != null   &&  alarm.getAlarmSeverity() == AlarmSeverity.UNDEFINED))
						{
						    value = DISCONNECTED;
						    Logger.getLogger(getClass().getName()).log(Level.WARNING,
						            "PV {0} disconnected", getName());
						}
						else
						{
    						Logger.getLogger(getClass().getName()).log(Level.FINER,
    					        "PV {0} received {1}", new Object[] { getName(), value });
    						
    						if (value == null)
    							value = DISCONNECTED;
    						
    						if (TREAD_BYTES_AS_STRING  &&
    						    value instanceof VByteArray)
    						{
    						    is_byte_array = true;
    						    final VByteArray barray = (VByteArray) value;
    						    value = ValueFactory.newVString(
    					            ByteHelper.toString(barray), (Alarm)barray, (Time)barray);
    
    						    Logger.getLogger(getClass().getName()).log(Level.FINER,
    	                              "PV BYTE[] converted to {0}", value);
    						}
						}
					}
				}
				fireDeviceUpdate();
			}
		};
		final PVWriterListener<? extends Object> write_listener = new PVWriterListener<Object>()
        {
            @Override
            public void pvChanged(PVWriterEvent<Object> event)
            {
                if (use_put_callback && event.isWriteSucceeded())
                    synchronized (PVDevice.this)
                    {
                        received_put_callback = true;
                        PVDevice.this.notifyAll();
                    }
            }
        };
		synchronized (this)
		{
            pv = PVManager
		        .readAndWrite(latestValueOf(vType(getName())))
		        .readListener(listener)
		        .writeListener(write_listener)
		        .asynchWriteAndMaxReadRate(ofSeconds(ScanSystemPreferences.getMinPVUpdatePeriod()));
		}
	}

	/** {@inheritDoc} */
    @Override
    public synchronized boolean isReady()
    {
        return value != DISCONNECTED  &&  pv != null  && pv.isConnected();
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
		final PV<VType, Object> copy;
		synchronized (this)
		{
			copy = pv;
		}
		copy.close();
		synchronized (this)
		{
			pv = null;
			value = DISCONNECTED;
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

	/** Write value to device, with special handling of EPICS BYTE[] as String 
     *  @param value Value to write (Double, String)
     *  @throws Exception on error: Cannot write, ...
     */
	@Override
    public void write(Object value) throws Exception
    {
	    if (is_byte_array  &&  value instanceof String)
	        value = ByteHelper.toBytes((String) value);

	    final PV<VType, Object> pv; // Copy to access PV outside of lock
	    synchronized (this)
		{
	        pv = this.pv;
	        if (use_put_callback)
	            received_put_callback = false;
		}
		pv.write(value);
		if (use_put_callback)
		{
		    synchronized (this)
            {
		        // TODO Timeout for put-callback
                while (! received_put_callback)
                    wait();
            }
		}
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Writing: PV {0} = {1}",
				new Object[] { getName(), value });
    }
}
