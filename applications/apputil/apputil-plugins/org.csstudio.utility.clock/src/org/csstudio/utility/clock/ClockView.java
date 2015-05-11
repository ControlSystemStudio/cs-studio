/*******************************************************************************
 * Copyright (c) 2010, 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.clock;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import static org.csstudio.utility.clock.preferences.PreferencePage.DEFAULT_HOURS;

import static java.util.Objects.isNull;

/** Part for the clock.
 *  @author Kay Kasemir
 */
@SuppressWarnings("restriction")
public class ClockView
{
    /** Fill the view. */
    @PostConstruct @Optional
    public void createPartControl(final Composite parent,
            @Preference(value = "hours") final String hours_pref)
    {
        parent.setLayout(new FillLayout());
        final int hours = isNull(hours_pref) ? DEFAULT_HOURS : Integer.parseInt(hours_pref);
        new ClockWidget(hours, parent, 0);
    }
}
