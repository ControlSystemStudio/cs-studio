package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.swt.chart.TraceType;
import org.junit.Test;

/** (Headless) JUnit Plug-inTest for the PVModelItem
 *  <p>
 *  Requires test database or 'excas' to run.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVModelItemTest
{
    @Test
    public void testModelItemScan() throws Exception
    {
        final Model model = new Model();
        final PVModelItem item = new PVModelItem(model, "fred",
                        1024, 0, 0, 0, true, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false,
                        IPVModelItem.RequestType.OPTIMIZED);
        item.start();
        final int num = 20;
        // 'Scan' the item once per second
        for (int i = 0; i < num; ++i)
        {
            Thread.sleep(1000);
            System.out.format("scan %3d / %s\n", i+1, num);
            ITimestamp now = TimestampFactory.now();
            item.addCurrentValueToSamples(now);
            if (item.getSamples().size() >= 5)
                break;
        }
        item.stop();

        final IModelSamples samples = item.getSamples();
        final int N = samples.size();
        if (N < 5)
            throw new Exception("Only " + N + " values?");
            
        for (int i=0; i<N; ++i)
        {
            ModelSample sample = samples.get(i);
            System.out.println(sample.toString());
        }
    }
}
