package org.csstudio.sns.mpsbypasses.modes;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Read beam mode from MPS PVs
 *
 *  @author Delphy Armstrong - Original RTDL_Switch_Modes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamModeMonitor implements PVListener
{
	final private BeamModeListener listener;

	final private PV[] rtdl_pvs;
	final private PV[] switch_pvs;

	private volatile BeamMode rtdl_mode = null;
	private volatile BeamMode switch_mode = null;

	/** Initialize
	 *  @throws Exception on error
	 */
	public BeamModeMonitor(final BeamModeListener listener) throws Exception
	{
		this.listener = listener;
		// Create PVs
		final BeamMode[] modes = BeamMode.values();
		rtdl_pvs = new PV[modes.length];
		switch_pvs = new PV[modes.length];
		for (int i=0; i<modes.length; ++i)
		{
			rtdl_pvs[i] = PVFactory.createPV("ICS_MPS:RTDL_BmMd:" + modes[i].getSignal());
			switch_pvs[i] = PVFactory.createPV("ICS_MPS:Switch_BmMd:" + modes[i].getSignal());
			rtdl_pvs[i].addListener(this);
			switch_pvs[i].addListener(this);
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
		BeamMode new_rtdl = null;
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
				new_rtdl = BeamMode.values()[i];
			}
		}

		// Decode MPS switch PVs
		BeamMode new_switch = null;
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
				new_switch = BeamMode.values()[i];
			}
		}

		updateModes(new_rtdl, new_switch);
    }

	/** Update modes and notify listeners on change
	 *  @param new_rtdl_mode
	 *  @param new_switch_mode
	 */
	private void updateModes(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
		if (new_rtdl_mode == rtdl_mode  &&  new_switch_mode == switch_mode)
			return;
		rtdl_mode = new_rtdl_mode;
		switch_mode = new_switch_mode;
		listener.beamModeUpdate(new_rtdl_mode, new_switch_mode);
    }
}
