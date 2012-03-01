package org.csstudio.sns.mpsbypasses.modes;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Read machine mode from MPS PVs
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MachineModeMonitor implements PVListener
{
	final private MachineModeListener listener;

	final private PV[] rtdl_pvs;
	final private PV[] switch_pvs;

	private volatile MachineMode rtdl_mode = null;
	private volatile MachineMode switch_mode = null;

	/** Initialize
	 *  @throws Exception on error
	 */
	public MachineModeMonitor(final MachineModeListener listener) throws Exception
	{
		this.listener = listener;
		// Create PVs
		final MachineMode[] modes = MachineMode.values();
		rtdl_pvs = new PV[modes.length - 1];
		switch_pvs = new PV[modes.length - 1];
		for (int i=1; i<modes.length; ++i)
		{
			rtdl_pvs[i-1] = PVFactory.createPV("ICS_MPS:RTDL_MachMd:" + modes[i].name());
			switch_pvs[i-1] = PVFactory.createPV("ICS_MPS:Switch_MachMd:" + modes[i].name());
			rtdl_pvs[i-1].addListener(this);
			switch_pvs[i-1].addListener(this);
		}
	}

	/** Connect PVs
	 *  @throws Exception on error
	 */
	public void start() throws Exception
	{
		for (PV pv : rtdl_pvs)
			pv.start();
		for (PV pv : switch_pvs)
			pv.start();
	}

	/** Disconnect PVs */
	public void stop()
	{
		for (PV pv : rtdl_pvs)
			pv.stop();
		for (PV pv : switch_pvs)
			pv.stop();
		updateModes(null, null);
	}

	/** {@inheritDoc} */
	@Override
    public void pvDisconnected(final PV any_pv)
    {
		updateModes(null, null);
    }

	/** {@inheritDoc} */
	@Override
    public void pvValueUpdate(final PV pv)
    {
		// System.out.println(pv.getName() + " = " + pv.getValue() + " (" + ValueUtil.getDouble(pv.getValue()) + ")");

		// Decode RTDL PVs
		MachineMode new_rtdl = null;
		for (int i=0; i<rtdl_pvs.length; ++i)
		{
			final IValue value = rtdl_pvs[i].getValue();
			if (value == null)
			{	// Any PV missing -> cannot be certain about mode
				new_rtdl = null;
				break;
			}
			final boolean active = ValueUtil.getDouble(value) > 0.0;
			if (active)
			{
				if (new_rtdl != null)
				{	// More than one mode at the same time?
					new_rtdl = null;
					break;
				}
				new_rtdl = MachineMode.values()[i+1];
			}
		}

		// Decode MPS switch PVs
		MachineMode new_switch = null;
		for (int i=0; i<switch_pvs.length; ++i)
		{
			final IValue value = switch_pvs[i].getValue();
			if (value == null)
			{	// Any PV missing -> cannot be certain about mode
				new_switch = null;
				break;
			}
			// Active low!
			final boolean active = ValueUtil.getDouble(value) < 1.0;
			if (active)
			{
				if (new_switch != null)
				{	// More than one mode at the same time?
					new_switch = null;
					break;
				}
				new_switch = MachineMode.values()[i+1];
			}
		}

		updateModes(new_rtdl, new_switch);
    }

	/** Update modes and notify listeners on change
	 *  @param new_rtdl_mode
	 *  @param new_switch_mode
	 */
	private void updateModes(final MachineMode new_rtdl_mode, final MachineMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.machineModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}
