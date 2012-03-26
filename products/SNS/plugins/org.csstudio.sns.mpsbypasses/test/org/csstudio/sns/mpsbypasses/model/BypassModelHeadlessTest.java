package org.csstudio.sns.mpsbypasses.model;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.sns.mpsbypasses.modes.MachineMode;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link BypassModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BypassModelHeadlessTest implements BypassModelListener
{
	final private AtomicInteger updates = new AtomicInteger(0);

	@Test
	public void testBypassModel() throws Exception
	{
		final BypassModel model = new BypassModel();
		model.addListener(this);

		// This will take some time, then call modelLoaded:
		System.out.println("Loading RDB data...");
		model.selectMachineMode(MachineMode.Site);

		// Allow model to 'run' for a while, connect to PVs and send updates
		Thread.sleep(5000);

		model.stop();
		// Should have had some updates
		final int received = updates.get();
		System.out.println("Got " + received + " updates");
		assertTrue(received > 0);
	}

	@Override
    public void modelLoaded(final BypassModel model, final Exception error)
    {
		if (error != null)
			error.printStackTrace();
		else
		{
			System.out.println("RDB data arrived, starting model...");
			try
			{
				model.start();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
    }

	@Override
    public void bypassesChanged()
    {
	    // Ignore
    }

	@Override
    public void bypassChanged(final Bypass bypass)
    {
		updates.incrementAndGet();
		if (bypass.getState() != BypassState.Disconnected)
			System.out.println(bypass);
    }
}
