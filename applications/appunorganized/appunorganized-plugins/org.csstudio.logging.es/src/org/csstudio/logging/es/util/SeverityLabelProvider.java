/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.util;

import org.csstudio.logging.es.model.EventLogMessage;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * CellLabelProvider that fills cell with property of a Message and provides
 * coloring assuming that the column displays a severity.
 *
 * @author Kay Kasemir
 */
public class SeverityLabelProvider extends PropertyLabelProvider
{
    /** Mapping of severities to colors */
    private final SeverityColumnPreference color_prefs;

    /**
     * Constructor
     *
     * @param property
     *            Message property to display in column
     * @param parent
     *            Parent widget, used to register DisposeListener because we
     *            need to dispose the colors
     * @throws Exception
     */
    public SeverityLabelProvider(final String property, final Composite parent)
            throws Exception
    {
        super(property);
        this.color_prefs = new SeverityColumnPreference(parent);
    }

    @Override
    public void update(ViewerCell cell)
    {
        EventLogMessage message = (EventLogMessage) cell.getElement();
        String severity = message.getPropertyValue(this.property);
        cell.setText(severity);
        Color color = this.color_prefs.getColor(severity);
        if (color != null)
        {
            cell.setBackground(color);
        }
    }
}
