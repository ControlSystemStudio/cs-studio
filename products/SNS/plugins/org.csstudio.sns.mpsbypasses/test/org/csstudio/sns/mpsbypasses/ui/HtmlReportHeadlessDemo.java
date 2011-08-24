package org.csstudio.sns.mpsbypasses.ui;

import static org.junit.Assert.assertTrue;

import org.csstudio.sns.mpsbypasses.model.Bypass;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.csstudio.sns.mpsbypasses.model.BypassModelListener;
import org.csstudio.sns.mpsbypasses.modes.MachineMode;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the {@link HtmlReport}
 *  
 *  @author Kay Kasemir
 */
public class HtmlReportHeadlessDemo implements BypassModelListener
{
	private boolean have_model_info = false;

	@Test
	public void demoHtmlReport() throws Exception
	{
		final BypassModel model = new BypassModel();
		
		model.addListener(this);
		model.selectMachineMode(MachineMode.Site);
		
		// Wait 30 secs to read RDB information
		synchronized (this)
        {
	        for (int i=0; i<30; ++i)
	        	if (have_model_info)
	        		break;
	        	else
	        		wait(1000);
        }
		assertTrue(have_model_info);
		
		// Wait a little more for bypass info PVs to connect
		Thread.sleep(5000);
		
		new HtmlReport(System.out, model).write();
		System.out.flush();
		
		model.stop();
	}

	@Override
    public void bypassChanged(final Bypass bypass)
    {
	    // Ignore
    }	
	
	@Override
    public void bypassesChanged()
    {
	    // Ignore
    }

	@Override
    public synchronized void modelLoaded(final BypassModel model, final Exception error)
    {
		have_model_info = error == null;
		if (have_model_info)
		{
			try
            {
	            model.start();
            }
            catch (Exception e)
            {
	            e.printStackTrace();
            }
		}
		notifyAll();
    }
}
