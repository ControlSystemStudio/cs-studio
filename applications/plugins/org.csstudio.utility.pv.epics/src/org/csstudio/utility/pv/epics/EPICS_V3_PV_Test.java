package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Test;

/** Test of the PV interface for a hardcoded implementation
 *  so that it can run as a unit test without Eclipse plugin loader.
 *  
 *  These tests require the soft-IOC database from lib/test.db.
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_Test
{
    /** Get a PV.
     *  
     *  <b>This is where the implementation is hard-coded!</b>
     *  
     *  @return PV
     */
    static private PV getPV(final String name)
    {
        return new EPICS_V3_PV(name);
    }
    
    /** Update counter for TestListener */
    private AtomicInteger updates = new AtomicInteger();
    
    /** Listener that dumps info and counts updates */
    class TestListener implements PVListener
    {
        private String name;
        
        TestListener(String name)
        {
            this.name = name;
        }
        
        public void pvValueUpdate(PV pv)
        {
            updates.addAndGet(1);
            IValue v = pv.getValue();
            System.out.println(name + ": "
                    + pv.getName() + ", "
                    + v.getTime() + " "
                    + v);
        }

        public void pvDisconnected(PV pv)
        {
            System.out.println(name + ": "
                    + pv.getName() + " disconnected");
        }
    }

    @Test
    public void testSinglePVStartStop() throws Exception
    {
        PV pv = getPV("fred");
        pv.addListener(new TestListener("A"));

        pv.start();
        int wait = 10;
        while (wait > 0)
        {
            if (updates.get() > 2)
                break;
            Thread.sleep(1000);
            --wait;
        }
        // Did we get anything?
        assertTrue(updates.get() > 2);
        pv.stop();
        int old = updates.get();
        wait = 10;
        while (wait > 0)
        {
            Thread.sleep(1000);
            assertEquals("updates should stop", old, updates.get());
            --wait;
        }
    }

    @Test
    public void testMetaData() throws Exception
    {
        PV pv = getPV("fred");
        pv.addListener(new TestListener("A"));
        pv.start();
        try
        {
            int wait = 10;
            while (wait > 0)
            {
                if (updates.get() > 2)
                    break;
                Thread.sleep(1000);
                --wait;
            }
            // Did we get anything?
            assertTrue(updates.get() > 2);
            // Meta info as expected?
            INumericMetaData meta = (INumericMetaData)pv.getValue().getMetaData();
            assertEquals("furlong", meta.getUnits());
            assertEquals(4, meta.getPrecision());
        }
        finally
        {
            pv.stop();
        }
    }

    
    @Test
    public void testLong() throws Exception
    {
        PV pva = getPV("long_fred");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        assertTrue(pva.isConnected());
        final IValue value = pva.getValue();
        System.out.println("'long' PV value: " + value);
        assertTrue(value instanceof ILongValue);
        
        pva.stop();
    }

    @Test
    public void testMultiplePVs() throws Exception
    {
        PV pva = getPV("fred");
        PV pvb = getPV("janet");
        
        pva.addListener(new TestListener("A"));
        pvb.addListener(new TestListener("B"));

        updates.set(0);
        pva.start();
        pvb.start();
        int wait = 10;
        while (wait > 0)
        {
            if (updates.get() > 4)
                break;
            Thread.sleep(1000);
            --wait;
        }
        assertTrue(updates.get() > 4);
        pvb.stop();
        pva.stop();
    }

    @Test
    public void testDuplicatePVs() throws Exception
    {
        PV pva = getPV("fred");
        PV pvb = getPV("fred");
        
        pva.addListener(new TestListener("A"));
        pvb.addListener(new TestListener("B"));

        updates.set(0);
        pva.start();
        pvb.start();
        int wait = 10;
        while (wait > 0)
        {
            if (updates.get() > 4)
                break;
            Thread.sleep(1000);
            --wait;
        }
        assertTrue(updates.get() > 4);
        pvb.stop();
        Thread.sleep(4000);
        pva.stop();
    }

    @Test
    public void testEnum() throws Exception
    {
        PV pva = getPV("fred.SCAN");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        assertTrue(pva.isConnected());
        assertTrue(pva.getValue() instanceof IEnumeratedValue);
        IEnumeratedValue e = (IEnumeratedValue) pva.getValue();
        assertEquals(6, e.getValue());
        assertEquals("1 second", e.format());
        
        pva.stop();

        pva = getPV("enum");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        assertTrue(pva.isConnected());
        assertTrue(pva.getValue() instanceof IEnumeratedValue);
        e = (IEnumeratedValue) pva.getValue();
        assertEquals(1, e.getValue());
        assertEquals("one", e.format());
        assertTrue(e.getMetaData() instanceof IEnumeratedMetaData);
        IEnumeratedMetaData meta = (IEnumeratedMetaData) e.getMetaData();
        assertEquals(4, meta.getStates().length);
        assertEquals("zero", meta.getStates()[0]);
        
        pva.stop();
    }


    @Test
    public void testDblWaveform() throws Exception
    {
        PV pva = getPV("hist");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        assertTrue(pva.isConnected());
        final IValue value = pva.getValue();
        assertTrue(value instanceof IDoubleValue);
        double dbl[] = ((IDoubleValue) value).getValues();
        assertEquals(50, dbl.length);
        System.out.println(value);
        
        pva.stop();
    }

    @Test
    public void testLongWaveform() throws Exception
    {
        PV pva = getPV("longs");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        assertTrue(pva.isConnected());
        final IValue value = pva.getValue();
        assertTrue(value instanceof ILongValue);
        long longs[] = ((ILongValue) value).getValues();
        assertEquals(50, longs.length);
        System.out.println(value);
        
        pva.stop();
    }
}
