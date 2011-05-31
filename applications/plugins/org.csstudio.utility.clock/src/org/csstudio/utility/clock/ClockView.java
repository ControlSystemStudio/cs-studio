/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.clock;

import org.csstudio.utility.clock.preferences.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** ViewPart for the clock.
 *  @author Kay Kasemir
 */
public class ClockView extends ViewPart
{
	public static final String ID = ClockView.class.getName();
    // The one and only widget in this view
    private ClockWidget clock;

    /** Fill the view. */
    @Override
    public void createPartControl(Composite parent)
    {
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        parent.setLayout(gl);
        GridData gd;

        clock = new ClockWidget(PreferencePage.getHours(), parent, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        clock.setLayoutData(gd);
    }

    /** Set focus on clock, though that's a NOP. */
    @Override
    public void setFocus()
    {
        clock.setFocus();
    }
}
