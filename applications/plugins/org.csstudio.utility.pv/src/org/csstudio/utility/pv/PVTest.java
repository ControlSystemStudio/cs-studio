package org.csstudio.utility.pv;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import junit.framework.Assert;

/** These tests require the 'excas' server from EPICS base
 * 
 *  @author Kay Kasemir
 */
public class PVTest extends TestCase
{
    private AtomicInteger updates = new AtomicInteger();
    
    class TestListener implements PVListener
    {
        private String name;
        
        TestListener(String name)
        {
            this.name = name;
        }
        
        /** @see org.csstudio.pvtable.pv.PVListener#pvValueUpdate(org.csstudio.pvtable.pv.PV) */
        public void pvValueUpdate(PV pv)
        {
            updates.addAndGet(1);
            System.out.println(name + ": "
                    + pv.getName() + ", "
                    + pv.getTime().toString() + " "
                    + pv.getValue());
        }

        /** @see org.csstudio.pvtable.pv.PVListener#pvDisconnected(org.csstudio.pvtable.pv.PV) */
        public void pvDisconnected(PV pv)
        {
            System.out.println(name + ": "
                    + pv.getName() + " disconnected");
        }
    }

    public void testSinglePV() throws Exception
    {
        PV pv = new EPICS_V3_PV("fred");
        
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
        Assert.assertTrue(updates.get() > 2);
        // Meta info as expected?
        Assert.assertEquals("furlong", pv.getUnits());
        Assert.assertEquals(4, pv.getPrecision());
        
        pv.stop();
    }

    public void testMultiplePVs() throws Exception
    {
        PV pva = new EPICS_V3_PV("fred");
        PV pvb = new EPICS_V3_PV("janet");
        
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
        Assert.assertTrue(updates.get() > 4);
        pvb.stop();
        pva.stop();
    }

    public void testDuplicatePVs() throws Exception
    {
        PV pva = new EPICS_V3_PV("fred");
        PV pvb = new EPICS_V3_PV("fred");
        
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
        Assert.assertTrue(updates.get() > 4);
        pvb.stop();
        Thread.sleep(4000);
        pva.stop();
    }
}
