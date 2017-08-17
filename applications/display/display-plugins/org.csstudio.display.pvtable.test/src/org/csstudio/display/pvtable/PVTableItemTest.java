/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableItemListener;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VType;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of {@link PVTableItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableItemTest implements PVTableItemListener
{
    /** Get VType as double or NaN if not possible
     *  @param value {@link VType}
     *  @return double or NaN
     */
    final public static double toDouble(final VType value)
    {
        if (value instanceof VNumber)
            return ((VNumber)value).getValue().doubleValue();
        if (value instanceof VEnum)
            return ((VEnum)value).getIndex();
        return Double.NaN;
    }

    @Before
    public void setup()
    {
        TestSettings.setup();
    }

    @Override
    public void tableItemSelectionChanged(final PVTableItem item)
    {
        System.out.println(item.getName() + (item.isSelected() ? " is selected" : " is not selected"));
    }

    @Override
    public void tableItemChanged(final PVTableItem item)
    {
        System.out.println(item);
        synchronized (item)
        {
            item.notifyAll();
        }
    }


    @Test(timeout=8000)
    public void testPVTableItem() throws Exception
    {

        final PV pv = PVPool.getPV(TestSettings.NAME);
        pv.write(3.14);

        final PVTableItem item = new PVTableItem(TestSettings.NAME, Preferences.getTolerance(), null, this);
        item.setTolerance(0.1);


        // Get initial value
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 3.14)
                item.wait(100);
        }
        assertThat(toDouble(item.getValue()), equalTo(3.14));

        // There is no saved value, so also no change
        assertThat(item.isChanged(), equalTo(false));

        // Current matches saved value
        item.save();
        assertThat(item.isChanged(), equalTo(false));

        // Receive update, but within tolerance
        pv.write(3.15);
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 3.15)
                item.wait(100);
        }
        assertThat(toDouble(item.getValue()), equalTo(3.15));
        assertThat(item.isChanged(), equalTo(false));

        // Receive update beyond tolerance
        pv.write(6.28);
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 6.28)
                item.wait(100);
        }
        assertThat(toDouble(item.getValue()), equalTo(6.28));

        // Current no longer matches saved value
        System.out.println("Saved: " + item.getSavedValue());
        assertThat(item.isChanged(), equalTo(true));

        // Value changes back on its own
        pv.write(3.14);
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 3.14)
                item.wait(100);
        }
        assertThat(toDouble(item.getValue()), equalTo(3.14));
        assertThat(item.isChanged(), equalTo(false));

        // PV changes to a new value
        pv.write(42.0);
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 42.0)
                item.wait(100);
        }
        assertThat(toDouble(item.getValue()), equalTo(42.0));
        assertThat(item.isChanged(), equalTo(true));

        // Restore the saved value
        assertThat(item.getSavedValue().get().toString(), equalTo("3.14"));
        item.restore(0);
        System.out.println("Waiting for restore...");
        synchronized (item)
        {
            while (toDouble(item.getValue()) != 3.14)
                item.wait(100);
        }

        assertThat(item.isChanged(), equalTo(false));


        item.dispose();
    }
}
