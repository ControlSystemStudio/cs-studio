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
public class FormulaModelItemTest
{
    public void testModelItemScan() throws Exception
    {
        ModelItem.test_mode = true;
        
        ModelItem fred = new ModelItem(null, "fred",
                        1024, 0, 0, 0, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false);
        ModelItem janet = new ModelItem(null, "janet",
                        1024, 0, 0, 0, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false);
        fred.start();
        janet.start();
        final int num = 20;
        // 'Scan' the item once per second
        for (int i = 0; i < num; ++i)
        {
            Thread.sleep(1000);
            System.out.format("scan %3d / %s\n", i+1, num);
            ITimestamp now = TimestampFactory.now();
            fred.addCurrentValueToSamples(now);
            janet.addCurrentValueToSamples(now);
            if (fred.getSamples().size() >= 5)
                break;
        }
        janet.stop();
        fred.stop();

        IModelSamples samples = fred.getSamples();
        int N = samples.size();
        if (N < 5)
            throw new Exception("Only " + N + " values?");
            
        System.out.println("Original Samples for fred:");
        dumpSamples(samples);
        System.out.println("Original Samples for janet:");
        dumpSamples(janet.getSamples());
        
        System.out.println("Formula:");
        FormulaModelItem formula = new FormulaModelItem(null, "calc",
                        0, 0, 0, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false);
        formula.addInput(fred, fred.getName());
        formula.addInput(janet, janet.getName());
        formula.setFormula("1000*fred + janet");
        samples = formula.getSamples();
        dumpSamples(samples);
    }

    private void dumpSamples(IModelSamples samples)
    {
        for (int i=0; i<samples.size(); ++i)
        {
            ModelSample sample = samples.get(i);
            System.out.println(sample.toString());
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        FormulaModelItemTest test = new FormulaModelItemTest();
        test.testModelItemScan();
    }
}
