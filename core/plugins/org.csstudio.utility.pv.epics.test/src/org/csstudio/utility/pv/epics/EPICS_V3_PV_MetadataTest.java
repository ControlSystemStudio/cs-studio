/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.junit.Ignore;
import org.junit.Test;

/** Test of meta data
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV_MetadataTest implements PVListener
{
    /** Update counter */
    private int updates = 0;
    
    @Override
    public void pvValueUpdate(final PV pv)
    {
        final IValue v = pv.getValue();
        System.out.println(pv.getName() + ", " + v.getTime() + " " + v);
        synchronized (this)
        {
            ++updates;
            notifyAll();
        }
    }

    @Override
    public void pvDisconnected(final PV pv)
    {
        System.out.println(pv.getName() + " disconnected");
    }

    @Ignore
    @Test(timeout=3000)
    public void testMetaData() throws Exception
    {
        synchronized (this)
        {
            updates = 0;
        }
        final PV pv = TestUtil.getPV("fred");
        pv.addListener(this);
        pv.start();
        try
        {
            synchronized (this)
            {
                while (updates < 1)
                    wait();
            }
            // Did we get anything?
            final IValue value = pv.getValue();
            assertTrue(value != null);
            // Meta info as expected?
            assertTrue(value.getMetaData() instanceof INumericMetaData);
            final INumericMetaData meta = (INumericMetaData)value.getMetaData();
            System.out.println(meta);
            assertEquals("furlong", meta.getUnits());
            assertEquals(4, meta.getPrecision());
        }
        finally
        {
            pv.stop();
        }
    }

    /** Test of meta data changes
     * 
     *  <p>For R3.14.11 IOCs, this is only supported for the
     *  enumeration strings of MBBI/MBBO records, so this test
     *  tries to update the ONST of a record, expecting to get
     *  a new _value_. Before R3.14.11, this will fail.
     */
    @Test(timeout=10000)
    public void testMetaDataUpdate() throws Exception
    {
        synchronized (this)
        {
            updates = 0;
        }
        final PV meta_pv = TestUtil.getPV("enum.ONST");
        meta_pv.start();
        
        final PV pv = TestUtil.getPV("enum");
        pv.addListener(this);
        pv.start();
        try
        {
            // Wait for initial value
            synchronized (this)
            {
                while (updates < 1)
                    wait();
            }
            // Did we get anything?
            IValue value = pv.getValue();
            assertTrue(value != null);
            // Meta info as expected?
            assertTrue(value.getMetaData() instanceof IEnumeratedMetaData);
            IEnumeratedMetaData meta = (IEnumeratedMetaData)value.getMetaData();
            System.out.println(meta);
            assertEquals("one", meta.getState(1));
            assertEquals("one", value.format());
            
            // For IOCs, we may actually get another update because the meta data monitor
            // also sends an update. From the gateway, we will not get that meta data monitor...
            // Wait a little to get over this uncertainty
            Thread.sleep(1000);
            
            // Change the meta data, should get a value update
            synchronized (this)
            {
                updates = 0;
            }
            meta_pv.setValue("Uno!");
            synchronized (this)
            {
                while (updates < 1)
                    wait();
            }
            value = pv.getValue();
            assertTrue(value != null);
            meta = (IEnumeratedMetaData)value.getMetaData();
            System.out.println(meta);
            assertEquals("Uno!", meta.getState(1));
            assertEquals("Uno!", value.format());
            
            // Restore, assert that this is read back
            synchronized (this)
            {
                updates = 0;
            }
            meta_pv.setValue("one");
            synchronized (this)
            {
                while (updates < 1)
                    wait();
            }
            value = pv.getValue();
            assertTrue(value != null);
            meta = (IEnumeratedMetaData)value.getMetaData();
            System.out.println(meta);
            assertEquals("one", meta.getState(1));
            assertEquals("one", value.format());
        }
        finally
        {
            pv.stop();
            meta_pv.stop();
        }
    }
}
