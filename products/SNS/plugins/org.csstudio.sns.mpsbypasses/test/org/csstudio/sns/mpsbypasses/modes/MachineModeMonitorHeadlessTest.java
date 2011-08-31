package org.csstudio.sns.mpsbypasses.modes;

import static org.junit.Assert.*;

import org.junit.Test;

/** [Headless] JUnit test of the {@link MachineModeMonitor}
 *  @author Kay Kasemir
 */
public class MachineModeMonitorHeadlessTest implements MachineModeListener
{
	private MachineMode rtdl_mode = null;
	private MachineMode switch_mode = null;
	
	@Override
    public void machineModeUpdate(final MachineMode new_rtdl_mode, final MachineMode new_switch_mode)
    {
		synchronized (this)
        {
	        rtdl_mode = new_rtdl_mode;
	        switch_mode = new_switch_mode;
	        notifyAll();
        }
    }
	
	@Test
	public void testMachineModeMonitor() throws Exception
	{
		final MachineModeMonitor mm = new MachineModeMonitor(this);
		mm.start();
		// Wait 5 sec for mode info to arrive
        for (int i=0; i<5; ++i)
        {
    		synchronized (this)
            {
    			if (rtdl_mode != null   &&   switch_mode != null)
    			{
    				System.out.println("Mode is: RTDL " + rtdl_mode + ", MPS switches " + switch_mode);
    				break;
    			}
    			else
    				wait(1000);
	        }
        }
        synchronized (this)
        {
            assertNotNull(rtdl_mode);
            assertNotNull(switch_mode);
        }
		mm.stop();
        synchronized (this)
        {
            assertNull(rtdl_mode);
            assertNull(switch_mode);
        }
	}
}
