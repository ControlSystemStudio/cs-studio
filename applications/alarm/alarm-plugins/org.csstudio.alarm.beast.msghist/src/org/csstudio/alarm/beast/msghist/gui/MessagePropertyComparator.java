/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/** ViewerComparator (= table sorter) that compares a single property.
 *  @author Kay Kasemir
 */
public class MessagePropertyComparator extends ViewerComparator
{
    final private String property;
    final private boolean up;

    /** Initialize comparator
     *  @param property Name of the Message property to compare
     *  @param up Sort 'up' or 'down', ascending or descending?
     */
    public MessagePropertyComparator(final String property,
            final boolean up)
    {
        this.property = property;
        this.up = up;
    }

    /** {@inhericDoc} */
    @SuppressWarnings("nls")
    @Override
    public int compare(final Viewer viewer, final Object e1, final Object e2)
    {
        final Message msg1 = (Message) e1;
        final Message msg2 = (Message) e2;
        String prop1 = msg1.getProperty(property);
        String prop2 = msg2.getProperty(property);
        // Property strings may be null; fix to allow comparison
        if (prop1 == null)
        	prop1 = "";
        if (prop2 == null)
        	prop2 = "";
        if (up)
            return prop2.compareTo(prop1);
        return prop1.compareTo(prop2);
    }

    /** {@inhericDoc} */
    @Override
    public void sort(final Viewer viewer, final Object[] elements)
    {
        final BenchmarkTimer timer = new BenchmarkTimer();
        super.sort(viewer, elements);
        timer.stop();
        // Test results: When overall time from click to sorted table
        // took ~3 seconds, the sort itself was only <0.01 secs,
        // i.e. a tiny part of the problem.
//        System.out.println("Sort time for " + property
//        		+ " :" + timer.getSeconds() + " secs");;
    }
}
