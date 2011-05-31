/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Timer;

import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the FormulaItem
 *  @author Kay Kasemir
 *  FIXME (kasemir) : remove sysos, use assertions
 */
@SuppressWarnings("nls")
public class FormulaItemTest
{
    /** Time in seconds for each test */
    private static final double RUNTIME_SECS = 10.0;

    @Test
    public void checkPV() throws Exception
    {
        try
        {
            PVFactory.getSupportedPrefixes();
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Must run as JUnit *Plug-In* test to use PVFactory");
        }
    }

    /** Check trivial Formula */
    @Test
    public void testSimpleFormula() throws Exception
    {
        //System.out.println("*** Trivial Example");

        // Create a (local) PV that we can change as needed
        final PV pv = PVFactory.createPV("loc://num");
        pv.start();
        pv.setValue(new Double(21.0));

        // Now create model item for the same PV
        final Timer scan_timer = new Timer();
        final PVItem pv_item = new PVItem("loc://num", 0);
        pv_item.start(scan_timer);
        IDataProvider samples = pv_item.getSamples();
        //System.out.println(samples);
        // Should be the initial value and 'continuation' sample
        assertEquals(2, samples.getSize());

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pv_item, "pv"),
        };

        final FormulaItem formula = new FormulaItem("test", "pv * 2", inputs);
        //System.out.println("\nFormula: " + formula.getName());
        samples = formula.getSamples();
        //System.out.println(samples);
        // Should be pv * 2
        assertEquals(2, samples.getSize());
        assertEquals(42.0, samples.getSample(0).getYValue(), 0.001);
        assertEquals(42.0, samples.getSample(1).getYValue(), 0.001);

        // Formula does not re-evaluate...
        pv_item.getSamples().testAndClearNewSamplesFlag();
        assertFalse(formula.reevaluate());
        // ..until an input changes
        //System.out.println("Input changes:");
        pv.setValue(new Double(3.14/2));
        assertTrue(formula.reevaluate());
        samples = formula.getSamples();
        //System.out.println(samples);
        assertEquals(3, samples.getSize());
        assertEquals(3.14, samples.getSample(2).getYValue(), 0.001);
    }

    /** Check if Formula handles 'real' inputs */
    @Test
    public void testPVFormula() throws Exception
    {
        //System.out.println("\n\n*** Scanned samples: (" + RUNTIME_SECS + " secs)");
        final Timer scan_timer = new Timer();
        final PVItem pvs[] = new PVItem[]
        {
            new PVItem("sim://ramp(0,10,1,1)", 0.0),
            new PVItem("sim://ramp(0,10,1,2)", 0.0)
        };
        for (final PVItem pv : pvs) {
            pv.start(scan_timer);
        }
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        for (final PVItem pv : pvs) {
            pv.stop();
        }
        // Should have about 1 sample per second
        IDataProvider samples = pvs[0].getSamples();
        //System.out.println(samples);
        assertEquals(RUNTIME_SECS, samples.getSize(), 2.0);
        samples = pvs[1].getSamples();
        //System.out.println(samples);
        // Second input should have about half that
        assertEquals(RUNTIME_SECS/2, samples.getSize(), 2.0);

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pvs[0], "volt"),
            new FormulaInput(pvs[1], "curr"),
        };

        // Test the input's iterator
        //System.out.println("\nFirst channel samples via FormulaInput:");
        IValue next = inputs[0].first();
        while (next != null)
        {
            //System.out.println(next);
            next = inputs[0].next();
        }

        final FormulaItem formula = new FormulaItem("test", "volt * 1000 + curr", inputs);
        //System.out.println("\nFormula: " + formula.getName());
        //System.out.println(formula.getSamples());
        // Formula should produce about one sample for the first input
        assertEquals(RUNTIME_SECS, formula.getSamples().getSize(), 5.0);
        // Unclear how to check if each sample is computed correctly
    }

    /** Check if Formula handles samples with min/max/average */
    @Test
    public void testMinMax() throws Exception
    {
        //System.out.println("\n\n*** Min/Max Values");
        final PVItem pvs[] = new PVItem[]
        {
            new PVItem("const://a(1)", 0.0),
        };
        // Add archived data
        final ArrayList<IValue> data = new ArrayList<IValue>();
        for (int i=0; i<10; ++i)
        {
            data.add(
                ValueFactory.createMinMaxDoubleValue(TimestampFactory.fromDouble(i),
                        TestSampleBuilder.ok,
                        TestSampleBuilder.ok.toString(),
                        PlotSample.dummy_meta,
                        IValue.Quality.Interpolated,
                        new double[] { i }, i-1, i+1));
        }
        pvs[0].mergeArchivedSamples("Test", data);


        PlotSamples samples = pvs[0].getSamples();
        //System.out.println(samples);

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pvs[0], "a"),
        };

        final FormulaItem formula = new FormulaItem("test", "a*2", inputs);
        //System.out.println("\nFormula: " + formula.getName());
        samples = formula.getSamples();
        //System.out.println(samples);
        assertEquals(data.size() + 1, samples.getSize());
        assertTrue("Min/Max data", samples.getSample(0).getValue() instanceof IMinMaxDoubleValue);
    }
}
