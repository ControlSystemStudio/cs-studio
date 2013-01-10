/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the ModelListener
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelListenerHeadlessTest
{
    private int additions = 0;

    private int removals = 0;

    private int changes = 0;

    final private ModelListener listener = new ModelListener()
    {
        @Override
        public void changedUpdatePeriod()
        {
            System.out.println("Update period changed");
            ++changes;
        }

        @Override
        public void changedArchiveRescale()
        {
            ++changes;
        }

        @Override
        public void changedColors()
        {
            System.out.println("Colors changed");
            ++changes;
        }

        @Override
        public void changedTimerange()
        {
            System.out.println("Time range changed");
            ++changes;
        }

        @Override
        public void changedAxis(final AxisConfig axis)
        {
            System.out.println("Axis changed: " + axis);
        }

        @Override
        public void itemAdded(final ModelItem item)
        {
            System.out.println("Added " + item.getName());
            ++additions;
        }

        @Override
        public void itemRemoved(final ModelItem item)
        {
            System.out.println("Removed " + item.getName());
            ++removals;
        }

        @Override
        public void changedItemVisibility(final ModelItem item)
        {
            System.out.println(item.getName() + " became " + (item.isVisible() ? "visible" : "invisible"));
            ++changes;
        }

        @Override
        public void changedItemLook(final ModelItem item)
        {
            System.out.println("Change in look of " + item.getName());
            ++changes;
        }

        @Override
        public void changedItemDataConfig(final PVItem item)
        {
            System.out.println("Change in data config of " + item.getName());
            ++changes;
        }

        @Override
        public void scrollEnabled(final boolean scroll_enabled)
        {
            System.out.println("Scrolling turned " + (scroll_enabled ? "on" : "off"));
            ++changes;
        }
        
        @Override
        public void changedAnnotations() {
            // TODO Auto-generated method stub

        }

        @Override
        public void changedXYGraphConfig() {
            // TODO Auto-generated method stub

        }
    };

    /** Test if listener is invoked as expected */
    @Test
    public void testListener() throws Exception
    {
        final Model model = new Model();
        model.addListener(listener);

        // Add an item
        final PVItem item = new PVItem("Test", 2.0);
        assertEquals(0, additions);
        model.addItem(item);
        assertEquals(1, additions);

        // Cannot add the same item multiple times
        try
        {
            model.addItem(item);
            fail("Can add multiple times?");
        }
        catch (final RuntimeException ex)
        {
            // OK, expected this
        }

        // Change something
        item.setLineWidth(5);
        assertEquals(1, changes);

        // Setting to same value is no change
        item.setLineWidth(5);
        assertEquals(1, changes);

        // Change more
        item.setLineWidth(6);
        assertEquals(2, changes);

        item.setDisplayName("Fred");
        assertEquals(3, changes);

        item.addArchiveDataSource(new ArchiveDataSource("url", 1, "whatever"));
        assertEquals(4, changes);

        // Leave scroll mode 'as is'
        assertEquals(true, model.isScrollEnabled());
        model.enableScrolling(true);
        assertEquals(4, changes);

        // Change it
        model.enableScrolling(false);
        assertEquals(5, changes);

        // Change start/end
        model.setTimerange(model.getStartTime(), model.getEndTime());
        assertEquals(6, changes);

        model.setTimespan(120);
        assertEquals(7, changes);

        // Visibility
        item.setVisible(true);
        assertEquals(7, changes);
        item.setVisible(false);
        assertEquals(8, changes);
        item.setVisible(false);
        assertEquals(8, changes);

        // Change waveform index
        item.setWaveformIndex(1);
        assertEquals(9, changes);

        // Remove
        assertEquals(0, removals);
        model.removeItem(item);
        assertEquals(1, removals);

        // Cannot remove the same item multiple times
        try
        {
            model.removeItem(item);
            fail("Can remove multiple times?");
        }
        catch (final RuntimeException ex)
        {
            // OK, expected this
        }

        // Change model some more
        model.setUpdatePeriod(10.0);
        assertEquals(10, changes);

        model.setPlotBackground(new RGB(1,2,3));
        assertEquals(11, changes);
    }
}
