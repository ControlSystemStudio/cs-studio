/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.gui;

import org.csstudio.diag.pvutil.model.PVUtilDataAPI;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Combo;

/** Provides the Device for a specific Table row */
public class FECProvider implements ILazyContentProvider
{
    final private Combo fec_combo;

    public FECProvider(Combo fec_combo, PVUtilDataAPI util_model)
    {
        this.fec_combo = fec_combo;

    }

    /** {@inheritDoc} */
    @Override
    public void updateElement(int index)
    {
        fec_combo.getText();

    }

    @Override
    public void dispose()
    {
        // NOP
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // NOP
    }
}
