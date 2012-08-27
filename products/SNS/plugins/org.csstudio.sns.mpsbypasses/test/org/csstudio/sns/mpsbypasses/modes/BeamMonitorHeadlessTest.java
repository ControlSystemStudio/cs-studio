package org.csstudio.sns.mpsbypasses.modes;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/** [Headless] JUnit test of the {@link BeamModeMonitor}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamMonitorHeadlessTest implements BeamModeListener
{
    final private CountDownLatch done = new CountDownLatch(1);

	@Override
    public void beamModeUpdate(final BeamMode rtdl_mode, final BeamMode switch_mode)
    {
	    System.out.println("Mode is: RTDL " + rtdl_mode + ", MPS switches " + switch_mode);
	    if (rtdl_mode != null  &&  switch_mode != null)
	        done.countDown();
    }

	@Test
	public void testBeamModeMonitor() throws Exception
	{
		final BeamModeMonitor mm = new BeamModeMonitor(this);
		mm.start();
		done.await(5, TimeUnit.SECONDS);
		mm.stop();
		assertEquals(done.getCount(), 0);
	}
}
