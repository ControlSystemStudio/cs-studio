package org.csstudio.sns.mpsbypasses.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link Bypass}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassHeadlessTest implements BypassListener
{
	@Override
    public void bypassChanged(final Bypass info)
    {
		System.out.println(info.getName() + " is " + info.getState());
		// Wake waitForState()
		synchronized (this)
        {
			notifyAll();
        }
    }

	@Test
	public void testBypass() throws Exception
	{
		// Local PVs to simulate the MPS PVs
		final PV jumper = PVFactory.createPV("loc://Test_Sys:Bypass1:FPLX_sw_jump_status");
		final PV mask = PVFactory.createPV("loc://Test_Sys:Bypass1:FPLX_swmask");
		jumper.start();
		mask.start();
		// Simulate Bypassed
		jumper.setValue(new Double(1.0));
		mask.setValue(new Double(1.0));

		// Prepare to check those simulated PVs
		final Request request = new Request("Fred", new Date());
		final Bypass info = new Bypass("loc://Test_Sys:Bypass1:FPLX", request, this);
		assertEquals("loc://Test_Sys:Bypass1", info.getName());

		// Initially disconnected
		assertEquals(BypassState.Disconnected, info.getState());

		// Should connect to PVs and get Bypassed state
		info.start();
		waitForState(info, BypassState.Bypassed);

		// Bypass still possible, but not used
		mask.setValue(new Double(0.0));
		waitForState(info, BypassState.Bypassable);

		// Not allowed
		jumper.setValue(new Double(0.0));
		waitForState(info, BypassState.NotBypassable);

		// .. yet active?
		mask.setValue(new Double(1.0));
		waitForState(info, BypassState.InError);

		// Stop should result in disconnect
		info.stop();
		assertEquals(BypassState.Disconnected, info.getState());

		// No more updates?
		mask.setValue(new Double(0.0));
		Thread.sleep(1000);
		assertEquals(BypassState.Disconnected, info.getState());
	}

	private void waitForState(final Bypass info, final BypassState desired) throws InterruptedException
    {
		BypassState state = null;
		// Allow a few seconds
		for (int s=0; s<5; ++s)
		{
			state = info.getState();
			if (state == desired)
				break;
			synchronized (this)
            {
				wait(1000);
            }
		}
		assertEquals(desired, state);
	}
}
