package org.csstudio.sns.mpsbypasses.modes;

/** Enumerations for the different RTDL Beam Modes
 *
 *  @author Delphy Armstrong - Original RTDL_BeamMode
 */
@SuppressWarnings("nls")
public enum BeamMode
{
	Off("Off", "Off"),
	StandBy("StandBy", "Standby"),
	uSec10("10uSec", "10 usec"),
	uSec50("50uSec", "50 usec"),
	uSec100("100uSec", "100 usec"),
	mSec1("1mSec", "1 msec"),
	FullPwr("FullPwr", "Full Power"),
	MPSTest("MPSTest", "MPS Text");

	/** PV signal name */
	final private String pv_signal;

	/** Human-readable representation */
	final private String label;

	/** Initialize
	 *  @param pv_signal PV signal name
	 *  @param label Human-readable representation
	 */
	private BeamMode(final String pv_signal, final String label)
	{
		this.pv_signal = pv_signal;
		this.label = label;
	}

	/** @return PV signal name */
	public String getSignal()
	{
		return pv_signal;
	}

	/** @return Human-readable representation */
	@Override
    public String toString()
	{
		return label;
	}
}


