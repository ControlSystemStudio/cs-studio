/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.apputil.ui.swt.DropdownToolbarAction;

/** View toolbar action to select the scan
 *  @author Kay Kasemir
 */
public class ScanSelectorAction extends DropdownToolbarAction
{
    /** Initialize */
    public ScanSelectorAction()
    {
        super("Scan", "Select a Scan");
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        return new String[] { "Scan 1", "Scan 2", "Scan 3" };
    }

    /** {@inheritDoc} */
    @Override
    public void handleSelection(final String item)
    {
        System.out.println(item);
    }
}
