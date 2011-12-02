/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Label provider for tree that has {@link ScanCommand} elements
 *  @author Kay Kasemir
 */
public class CommandTreeLabelProvider extends CellLabelProvider
{
    /** {@inheritDoc} */
    @Override
    public void update(final ViewerCell cell)
    {
        final ScanCommand command = (ScanCommand) cell.getElement();
        cell.setText(command.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTipText(Object element)
    {
        final ScanCommand command = (ScanCommand) element;
        String name = command.getClass().getName();
        final int sep = name.lastIndexOf('.');
        return name.substring(sep + 1);
    }
}
