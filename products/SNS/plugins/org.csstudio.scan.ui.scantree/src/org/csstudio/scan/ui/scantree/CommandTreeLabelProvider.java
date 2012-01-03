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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

/** Label provider for tree or table that has {@link ScanCommand} elements.
 *  @author Kay Kasemir
 */
public class CommandTreeLabelProvider extends CellLabelProvider implements ILabelProvider
{
    // CellLabelProvider is used by TreeViewer,
    // ILabelProvider is used by TableViewer
    /** {@inheritDoc} */
    @Override
    public String getText(final Object element)
    {
        final ScanCommand command = (ScanCommand) element;
        // Add space between image and text
        return " " + command.toString(); //$NON-NLS-1$
    }
    
    /** {@inheritDoc} */
    @Override
    public Image getImage(final Object element)
    {
        final ScanCommand command = (ScanCommand) element;
        return CommandImages.getImage(command);
    }
    
    /** {@inheritDoc} */
    @Override
    public void update(final ViewerCell cell)
    {
        final Object element = (ScanCommand) cell.getElement();
        cell.setText(getText(element));
        cell.setImage(getImage(element));
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTipText(final Object element)
    {
        final ScanCommand command = (ScanCommand) element;
        final String name = command.getClass().getName();
        final int sep = name.lastIndexOf('.');
        return name.substring(sep + 1);
    }
}
