/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.apputil.ui.swt.DropdownToolbarAction;

/** View toolbar action to select the 'X' value
 *  @author Kay Kasemir
 */
public class XValueSelectorAction extends DropdownToolbarAction
{
    /** Initialize */
    public XValueSelectorAction()
    {
        super("X", "Select device for horizontal axis");
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        return new String[] { "PV1", "PV2", "PV3" };
    }

    /** {@inheritDoc} */
    @Override
    public void handleSelection(final String item)
    {
        System.out.println(item);
    }
}
