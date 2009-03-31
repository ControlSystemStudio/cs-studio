package org.csstudio.utility.pv.epics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.junit.Test;

/** JUnit Plug-in test to check for JNI JCA memory leaks.
 * 
 *  Should use -pluginCustomization /path/to/test_customization
 *  to configure CA addr list and logging.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_MemTest
{
    private static final int TESTRUNS = 500;
    private static final String PV_NAME = "RFQ_Vac:Pump3:Pressure";

    @Test
    public void testBasicPVConnections() throws Exception
    {
        final PV pv = PVFactory.createPV(PV_NAME);
        
        for (int i=0; i<TESTRUNS; ++i)
        {
            //pv.addListener(this);
            pv.start();
            Thread.sleep(3*1000);
            //pv.removeListener(this);
            pv.stop();
            
            dumpMeminfo();
        }
    }

    private void dumpMeminfo()
    {
        final double MB = 1024.0*1024.0;
        final double free = Runtime.getRuntime().freeMemory() / MB;
        final double total = Runtime.getRuntime().totalMemory() / MB;
        final double max = Runtime.getRuntime().maxMemory() / MB;
        
        final DateFormat format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
        System.out.format("%s == Alarm Server Memory: Max %.2f MB, Free %.2f MB (%.1f %%), total %.2f MB (%.1f %%)\n",
                format.format(new Date()), max, free, 100.0*free/max, total, 100.0*total/max);
    }
}
