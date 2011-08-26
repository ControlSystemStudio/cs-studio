package org.csstudio.sns.mpsbypasses.modes;

/** Beam mode listener
 *  @author Kay Kasemir
 */
public interface BeamModeListener
{
	/** Invoked when the beam mode readbacks change
	 *  @param new_rtdl_mode Beam mode based on RTDL info
	 *  @param new_switch_mode Beam mode based on MPS switch readings
	 */
	public void beamModeUpdate(BeamMode new_rtdl_mode, BeamMode new_switch_mode);
}
