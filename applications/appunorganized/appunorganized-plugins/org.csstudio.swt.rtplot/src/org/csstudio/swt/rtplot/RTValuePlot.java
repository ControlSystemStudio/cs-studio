/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import org.eclipse.swt.widgets.Composite;

/** Real-time plot using numbers on the 'X' axis
 *  @author Kay Kasemir
 */
public class RTValuePlot extends RTPlot<Double>
{
    /** @param parent Parent widget */
    public RTValuePlot(final Composite parent)
    {
        super(parent, Double.class);
    }
}
