/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.csstudio.swt.xygraph.undo.XYGraphMemento;
import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the ModelListener
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto added test case for waveform index
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
        public void changedColors()
        {
            System.out.println("Colors changed");
            ++changes;
        }

        @Override
        public void changedAxis(final AxisConfig axis)
        {
            System.out.println("Axis changed: " + axis);
        }
        
//        @Override
//        public void changedAxes(final AxesConfig axes)
//        {
//            System.out.println("Axis changed: " + axes);
//        }
        
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
        public void changedItemDataConfig(final ModelItem item)
        {
            System.out.println("Change in data config of " + item.getName());
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

		@Override
		public void changedItemData(ModelItem item) {
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
        final ModelItem item = new ModelItem("Test");
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

        model.setPlotBackground(new RGB(1,2,3));
        assertEquals(11, changes);
    }
}
