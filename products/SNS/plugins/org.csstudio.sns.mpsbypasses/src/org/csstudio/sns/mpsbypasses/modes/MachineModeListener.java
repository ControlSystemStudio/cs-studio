package org.csstudio.sns.mpsbypasses.modes;

/** Machine mode listener
 *  @author Kay Kasemir
 */
public interface MachineModeListener
{
	/** Invoked when the machine mode readbacks change
	 *  @param new_rtdl_mode Machine mode based on RTDL info
	 *  @param new_switch_mode Machine mode based on MPS switch readings
	 */
	public void machineModeUpdate(MachineMode new_rtdl_mode, MachineMode new_switch_mode);
}
