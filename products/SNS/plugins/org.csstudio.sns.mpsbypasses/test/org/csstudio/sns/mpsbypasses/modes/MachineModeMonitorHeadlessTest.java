package org.csstudio.sns.mpsbypasses.modes;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/** [Headless] JUnit test of the {@link MachineModeMonitor}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MachineModeMonitorHeadlessTest implements MachineModeListener
{
    final private CountDownLatch done = new CountDownLatch(1);

	@Override
    public void machineModeUpdate(final MachineMode rtdl_mode, final MachineMode switch_mode)
    {
        System.out.println("Mode is: RTDL " + rtdl_mode + ", MPS switches " + switch_mode);
        if (rtdl_mode != null  &&  switch_mode != null)
            done.countDown();
    }

	@Test
	public void testMachineModeMonitor() throws Exception
	{
		final MachineModeMonitor mm = new MachineModeMonitor(this);
		mm.start();
		done.await(5, TimeUnit.SECONDS);
		mm.stop();
        assertEquals(done.getCount(), 0);
	}
}
