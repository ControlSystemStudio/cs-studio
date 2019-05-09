/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.util;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.csstudio.logging.es.Messages;
import org.csstudio.logging.es.model.EventLogMessage;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;

/**
 * CellLabelProvider that fills cell with a property of a Message.
 * 
 * @author Kay Kasemir
 */
public class PropertyLabelProvider extends CellLabelProvider
{
    final protected String property;

    /**
     * Create label provider for Message
     * 
     * @param property
     *            Name of property to display
     */
    public PropertyLabelProvider(final String property)
    {
        this.property = property;
    }

    /** Show all property/value pairs as tool-tip */
    @Override
    public String getToolTipText(final Object element)
    {
        final EventLogMessage message = (EventLogMessage) element;
        Iterator<String> i = message.getProperties();
        Iterable<String> iterable = () -> i;
        return StreamSupport.stream(iterable.spliterator(), false).sorted()
                .map(s -> {
                    return NLS.bind(Messages.PropertyValue_TTFmt, s,
                            message.getPropertyValue(s));
                }).collect(Collectors.joining("\n")); //$NON-NLS-1$
    }

    /**
     * Fill table cell
     * 
     * @see CellLabelProvider
     */
    @Override
    public void update(ViewerCell cell)
    {
        final EventLogMessage message = (EventLogMessage) cell.getElement();
        cell.setText(message.getPropertyValue(this.property));
    }

}
