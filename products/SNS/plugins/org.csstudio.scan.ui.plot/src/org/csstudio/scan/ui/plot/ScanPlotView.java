/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the scan plot
 *  @author Kay Kasemir
 */
public class ScanPlotView extends ViewPart
{
    public ScanPlotView()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(final Composite parent)
    {
        final GUI gui = GUI.forCanvas(parent);
        gui.addTrace();
    }

    @Override
    public void setFocus()
    {
        // NOP
    }
}
