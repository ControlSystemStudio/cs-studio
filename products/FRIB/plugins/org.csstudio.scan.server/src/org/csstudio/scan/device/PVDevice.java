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

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** {@link Device} that is connected to a Process Variable,
 *  supporting read and write access to that PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDevice extends Device
{
    /** 'compile time' option to treat byte arrays as string */
    final private static boolean TREAD_BYTES_AS_STRING = true;
    
	/** Value that is used to identify a disconnected PV */
	final private static VType DISCONNECTED =
			ValueFactory.newVString("Disconnected", ValueFactory.newAlarm(AlarmSeverity.INVALID, "Disconnected"), ValueFactory.timeNow());
	
	/** Is the underlying PV type a BYTE[]?
	 *  @see #TREAD_BYTES_AS_STRING
	 */
	private boolean is_byte_array = false;
	
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
						value = DISCONNECTED;
					else
					{
						value = pv.getValue();
						Logger.getLogger(getClass().getName()).log(Level.FINER,
					        "PV {0} received {1}", new Object[] { getInfo().getName(), value });
						
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
				fireDeviceUpdate();
			}
		};
		synchronized (this)
		{
			pv = PVManager.readAndWrite(latestValueOf(vType(getInfo().getName()))).readListener(listener).asynchWriteAndMaxReadRate(ofSeconds(0.1));
		}
	}

	   /** {@inheritDoc} */
    @Override
    public synchronized boolean isReady()
    {
        return value != DISCONNECTED  &&  pv != null  && pv.isConnected();
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
    public synchronized VType read() throws Exception
    {
		final VType current = this.value;
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Reading: PV {0} = {1}",
				new Object[] { getInfo().getName(), current });
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
	    synchronized (this)
		{
			pv.write(value);
		}
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Writing: PV {0} = {1}",
				new Object[] { getInfo().getName(), value });
    }

	/** @return Human-readable representation of this device */
    @Override
    public synchronized String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
    	if (pv == null)
            buf.append(", no PV");
    	else
    	{
    	    buf.append(", PV '").append(pv.getName()).append("'");

    	    final VType current;
    	    synchronized (this)
            {
                current = this.value;
            }
    	    buf.append(" = ").append(VTypeHelper.toString(current));
    	}
    	return buf.toString();
    }
}
