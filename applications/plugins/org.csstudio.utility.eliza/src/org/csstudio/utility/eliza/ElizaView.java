/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.eliza;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for ElizaGUI
 *  @author Kay Kasemir
 */
public class ElizaView extends ViewPart
{
    /** View ID */
    final public static String ID = "org.csstudio.utility.eliza.ElizaView"; //$NON-NLS-1$

    private ElizaGUI gui;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(Composite parent)
    {
        gui = new ElizaGUI(parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
