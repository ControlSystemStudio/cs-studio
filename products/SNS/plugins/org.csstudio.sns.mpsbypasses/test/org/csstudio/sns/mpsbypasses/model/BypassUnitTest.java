package org.csstudio.sns.mpsbypasses.model;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.loc.LocalDataSource;
import org.junit.Before;
import org.junit.Test;

/** JUnit Plug-in test of the {@link Bypass}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassUnitTest implements BypassListener
{
    @Before
    public void setup()
    {
        PVManager.setDefaultDataSource(new LocalDataSource());
    }

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
		// Prepare to check simulated PVs
		final Request request = new Request("Fred", new Date());
		final Bypass info = new Bypass("loc://Test_Sys:Bypass1:FPLX", request, this);
		assertEquals("loc://Test_Sys:Bypass1", info.getName());

		// Initially disconnected
		assertEquals(BypassState.Disconnected, info.getState());

		// Should connect to PVs and get Bypassed state
		info.start();

        // Local PVs to simulate the MPS PVs
        final PVWriter<Object> jumper = PVManager.write(channel("loc://Test_Sys:Bypass1:FPLX_sw_jump_status")).sync();
        final PVWriter<Object> mask = PVManager.write(channel("loc://Test_Sys:Bypass1:FPLX_swmask")).sync();

        // Simulate Bypassed
        jumper.write(1);
        mask.write(1);

		waitForState(info, BypassState.Bypassed);

		// Bypass still possible, but not used
		mask.write(0);
		waitForState(info, BypassState.Bypassable);

		// Not allowed
		jumper.write(0);
		waitForState(info, BypassState.NotBypassable);

		// .. yet active?
		mask.write(1);
		waitForState(info, BypassState.InError);

		// Stop should result in disconnect
		info.stop();
		assertEquals(BypassState.Disconnected, info.getState());

		// No more updates?
		mask.write(0);
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
