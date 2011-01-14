/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import org.csstudio.apputil.time.RelativeTime;

/** Listener interface for the RelativeTimeWidget.
 *  @author Kay Kasemir
 */
public interface RelativeTimeWidgetListener
{
    /** The user or another piece of code set the widget to a new time.
     *  @param source The affected widget.
     *  @param time The current relative time specification.
     */
    public void updatedTime(RelativeTimeWidget source, RelativeTime time);
}
