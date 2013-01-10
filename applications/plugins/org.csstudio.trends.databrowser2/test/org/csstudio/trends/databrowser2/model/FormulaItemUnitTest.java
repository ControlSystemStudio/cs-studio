/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.csstudio.utility.test.HamcrestMatchers.closeTo;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the FormulaItem
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FormulaItemUnitTest
{
    /** Time in seconds for each test */
    private static final double RUNTIME_SECS = 10.0;

    @Before
    public void setup()
    {
        TestHelper.setup();
    }
    
    
    @Test
    public void testLocalPVFormula() throws Exception
    {
        // Create a (local) PV that we can change as needed
        final PVWriter<Object> pv = PVManager.write(channel("loc://num(21.0)")).sync();
        
        // Now create model item for the same PV
        final Timer scan_timer = null;
        final PVItem pv_item = new PVItem("loc://num", 0);
        pv_item.start(scan_timer);

        // Should be the initial value and 'continuation' sample
        IDataProvider samples = pv_item.getSamples();
        while (samples.getSize() < 2)
        {
            Thread.sleep(100);
            samples = pv_item.getSamples();
        }
        System.out.println(samples);
        assertThat(samples.getSize(), equalTo(2));
        assertThat(samples.getSample(1).getYValue(), closeTo(21.0, 0.001));

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pv_item, "pv"),
        };

        final FormulaItem formula = new FormulaItem("test", "pv * 2", inputs);
        System.out.println("\nFormula: " + formula.getName());
        samples = formula.getSamples();
        System.out.println(samples);
        // Should be pv * 2, same number of samples as input
        assertThat(samples.getSize(), equalTo(pv_item.getSamples().getSize()));
        assertThat(samples.getSample(0).getYValue(), closeTo(42.0, 0.001));
        assertThat(samples.getSample(1).getYValue(), closeTo(42.0, 0.001));

        // Formula does not re-evaluate...
        pv_item.getSamples().testAndClearNewSamplesFlag();
        assertThat(formula.reevaluate(), equalTo(false));
        // ..until an input changes
        System.out.println("Input changes:");
        pv.write(3.14/2);
        while (pv_item.getSamples().getSize() != 3)
            Thread.sleep(100);
        
        assertThat(formula.reevaluate(), equalTo(true));
        samples = formula.getSamples();
        System.out.println(samples);
        assertEquals(3, samples.getSize());
        assertThat(samples.getSample(2).getYValue(), closeTo(3.14, 0.001));
    }


    @Test
    public void testTwoPVs() throws Exception
    {
        final Timer scan_timer = new Timer();
        final PVItem pvs[] = new PVItem[]
        {
            new PVItem("sim://ramp(0,10,1,0.5)", 0.0),
            new PVItem("sim://ramp(0,10,1,1)", 0.0)
        };
        for (final PVItem pv : pvs)
            pv.start(scan_timer);
        
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        for (final PVItem pv : pvs)
            pv.stop();
        
        // Should have about 2 samples per second
        IDataProvider samples = pvs[0].getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(2*RUNTIME_SECS, 2.0));
        samples = pvs[1].getSamples();
        System.out.println(samples);
        // Second input should have about half that
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS, 2.0));

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pvs[0], "volt"),
            new FormulaInput(pvs[1], "curr"),
        };

        // Test the input's iterator
        System.out.println("\nFirst channel samples via FormulaInput:");
        VType next = inputs[0].first();
        while (next != null)
        {
            System.out.println(next);
            next = inputs[0].next();
        }

        final FormulaItem formula = new FormulaItem("test", "volt * 1000 + curr", inputs);
        System.out.println("\nFormula: " + formula.getName());
        System.out.println(formula.getSamples());
        // Formula can produce about one sample for each update from the two input streams
        assertThat(formula.getSamples().getSize(), closeTo(pvs[0].getSamples().getSize() + pvs[1].getSamples().getSize() , 5.0));
        // Unclear how to check if each sample is computed correctly
    }

    /** Check if Formula handles samples with min/max/average */
    @Test
    public void testMinMax() throws Exception
    {
        final PVItem pvs[] = new PVItem[]
        {
            new PVItem("loc://a(1)", 0.0),
        };
        // Add archived data
        final List<VType> data = new ArrayList<VType>();
        final Timestamp start = Timestamp.now();
        for (int i=0; i<10; ++i)
            data.add(new ArchiveVStatistics(start.plus(TimeDuration.ofSeconds(10+i)),
                    AlarmSeverity.NONE, "",
                    ValueFactory.displayNone(),
                    i, i-1, i+1, 0, 1));
        pvs[0].mergeArchivedSamples("Test", data);

        PlotSamples samples = pvs[0].getSamples();
        System.out.println(samples);

        // Perform computation on inputs in formula
        final FormulaInput inputs[] = new FormulaInput[]
        {
            new FormulaInput(pvs[0], "a"),
        };

        final FormulaItem formula = new FormulaItem("test", "a*2", inputs);
        System.out.println("\nFormula: " + formula.getName());
        samples = formula.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(data.size(), 2));
        assertTrue("Min/Max data", samples.getSample(0).getValue() instanceof VStatistics);
    }
}
