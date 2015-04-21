/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.util.Calendar;

/** Listener interface for the CalendarWidget.
 *  @author Kay Kasemir
 */
public interface CalendarWidgetListener
{
    /** The user or another piece of code set the widget to a new time.
     *  @param source The affected widget.
     *  @param calendar The current date and time.
     */
    public void updatedCalendar(CalendarWidget source, Calendar calendar);
}
