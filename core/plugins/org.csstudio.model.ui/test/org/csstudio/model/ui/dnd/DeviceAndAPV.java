package org.csstudio.model.ui.dnd;

import java.io.Serializable;

import org.csstudio.model.DeviceName;
import org.csstudio.model.ProcessVariableName;

/** Example for application object that combines a device with a PV name
 *  @author Gabriele Carcassi
 */
@SuppressWarnings("nls")
public class DeviceAndAPV implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;

	private final DeviceName device;
	private final ProcessVariableName pv;

	public DeviceAndAPV(final DeviceName device, final ProcessVariableName pv)
	{
		this.device = device;
		this.pv = pv;
	}

	public DeviceName getDevice()
	{
		return device;
	}

	public ProcessVariableName getPv()
	{
		return pv;
	}

    @Override
    public String toString()
	{
	    return "Device " + device.getDeviceName() + " with PV " + pv.getProcessVariableName();
	}
}
