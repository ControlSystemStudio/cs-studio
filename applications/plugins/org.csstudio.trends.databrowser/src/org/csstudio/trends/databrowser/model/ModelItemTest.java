package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.swt.chart.TraceType;

/** Test for ModelItem
 *  <p>
 *  Requires test database or 'excas' to run.
 *  <p>
 *  Since the ModelItem uses an SWT Color,
 *  not sure how to run this as a Unit test,
 *  so it's an application for
 *  <pre> 
 *   "Run as .. SWT Application"
 *  </pre>
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelItemTest
{
    public void testModelItemScan() throws Exception
    {
        ModelItem.test_mode = true;
        
        ModelItem item = new ModelItem(null, "fred",
                        1024, 0, 0, 0, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false);
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

        IModelSamples samples = item.getSamples();
        final int N = samples.size();
        if (N < 5)
            throw new Exception("Only " + N + " values?");
            
        for (int i=0; i<N; ++i)
        {
            ModelSample sample = samples.get(i);
            System.out.println(sample.toString());
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        ModelItemTest test = new ModelItemTest();
        test.testModelItemScan();
    }
}
