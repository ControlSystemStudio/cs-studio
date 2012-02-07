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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** {@link Device} that is connected to a Process Variable,
 *  supporting read and write access to that PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDevice extends Device implements PVListener
{
	/** Underlying control system PV */
	final private PV pv;

	/** Value that is used to identify a disconnected PV */
	final private static IValue DISCONNECTED =
	        ValueFactory.createStringValue(TimestampFactory.now(),
	                ValueFactory.createInvalidSeverity(),
	                "Disconnected",
	                IValue.Quality.Original,
	                new String[] { "Disconnected" });

	/** Most recent value of the PV */
	private volatile IValue value = DISCONNECTED;

	/** Initialize
	 *  @param info {@link DeviceInfo}
	 *  @throws Exception on error during PV setup
	 */
	public PVDevice(final DeviceInfo info) throws Exception
    {
	    super(info);
	    pv = PVFactory.createPV(info.getName());
	    pv.addListener(this);
    }

	/** {@inheritDoc} */
	@Override
    public void start() throws Exception
	{
		pv.start();
	}

	   /** {@inheritDoc} */
    @Override
    public boolean isReady()
    {
        return value != DISCONNECTED  &&  pv.isConnected();
    }

	/** {@inheritDoc} */
	@Override
    public void stop()
	{
		pv.stop();
		value = DISCONNECTED;
	}

	/** {@inheritDoc} */
	@Override
    public void pvValueUpdate(PV pv)
    {
	    this.value = pv.getValue();
		fireDeviceUpdate();
    }

	/** {@inheritDoc} */
	@Override
    public void pvDisconnected(PV pv)
    {
		value = DISCONNECTED;
        fireDeviceUpdate();
    }

    /** {@inheritDoc} */
	@Override
    public IValue read() throws Exception
    {
		final IValue current = this.value;
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Reading: PV {0} = {1}",
				new Object[] { pv, current });
		return current;
    }

	/** {@inheritDoc} */
	@Override
    public void write(final Object value) throws Exception
    {
		pv.setValue(value);
		Logger.getLogger(getClass().getName()).log(Level.FINER, "Writing: PV {0} = {1}",
				new Object[] { pv, value });
    }

	/** @return Human-readable representation of this device */
    @Override
    public String toString()
    {
        return super.toString() + ", PV '" + pv.getName() + "'";
    }
}
