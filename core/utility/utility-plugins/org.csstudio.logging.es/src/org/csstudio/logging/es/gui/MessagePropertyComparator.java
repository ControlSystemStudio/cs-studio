/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.model.EventLogMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * ViewerComparator (= table sorter) that compares a single property.
 * 
 * @author Kay Kasemir
 */
public class MessagePropertyComparator extends ViewerComparator
{
    private final String property;
    private final boolean up;

    /**
     * Initialize comparator
     * 
     * @param property
     *            Name of the Message property to compare
     * @param up
     *            Sort 'up' or 'down', ascending or descending?
     */
    public MessagePropertyComparator(String property, boolean up)
    {
        this.property = property;
        this.up = up;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        EventLogMessage msg1 = (EventLogMessage) e1;
        EventLogMessage msg2 = (EventLogMessage) e2;
        // Property strings may be null; fix to allow comparison
        String prop1 = Optional.ofNullable(msg1.getPropertyValue(this.property))
                .orElse("");
        String prop2 = Optional.ofNullable(msg2.getPropertyValue(this.property))
                .orElse("");

        if (JMSLogMessage.SEVERITY.equals(this.property))
        {
            Level l1;
            try
            {
                l1 = Level.parse(prop1);
            }
            catch (IllegalArgumentException ex)
            {
                l1 = Level.OFF;
            }
            Level l2;
            try
            {
                l2 = Level.parse(prop2);
            }
            catch (IllegalArgumentException ex)
            {
                l2 = Level.OFF;
            }
            return (l2.intValue() - l1.intValue()) * (this.up ? 1 : -1);
        }

        return prop1.compareTo(prop2) * (this.up ? 1 : -1);
    }
}
