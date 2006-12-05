package org.csstudio.utility.pv.epics;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.utility.pv.EnumValue;
import org.csstudio.utility.pv.NumericMetaData;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;

/** These tests require the 'excas' server from EPICS base,
 *  or (better) the soft-IOC database from lib/test.db.
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
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
        NumericMetaData meta = (NumericMetaData)pv.getValue().getMeta();
        Assert.assertEquals("furlong", meta.getUnits());
        Assert.assertEquals(4, meta.getPrecision());
        
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

    public void testEnum() throws Exception
    {
        PV pva = new EPICS_V3_PV("fred.SCAN");
        
        pva.start();
        while (!pva.isConnected())
            Thread.sleep(100);
        Assert.assertTrue(pva.isConnected());
        Assert.assertTrue(pva.getValue() instanceof EnumValue);
        EnumValue e = (EnumValue) pva.getValue();
        Assert.assertEquals(6, e.toInt());
        Assert.assertEquals("1 second", e.toString());
        
        pva.stop();
    }
}
