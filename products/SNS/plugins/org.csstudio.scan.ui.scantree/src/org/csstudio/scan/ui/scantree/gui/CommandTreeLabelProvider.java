/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/** Label provider for tree or table that has {@link ScanCommand} elements.
 *  @author Kay Kasemir
 */
public class CommandTreeLabelProvider extends CellLabelProvider
{
    /** Active command that should be highlighted */
    final private AtomicReference<ScanCommand> active_command = new AtomicReference<ScanCommand>();

    /** @param command 'Active' command to highlight
     *  @return Previous active command
     */
    public synchronized ScanCommand setActiveCommand(final ScanCommand command)
    {
        return active_command.getAndSet(command);
    }

    /** @param command {@link ScanCommand} in current cell
     *  @return Text to display
     */
    public String getText(final ScanCommand command)
    {
        // Add space between image and text
        return " " + command.toString(); //$NON-NLS-1$
    }

    /** @param command {@link ScanCommand} in current cell
     *  @return Image to display
     */
    public Image getImage(final ScanCommand command)
    {
        try
        {
            return CommandsInfo.getInstance().getImage(command);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(final ViewerCell cell)
    {
        final ScanCommand command = (ScanCommand) cell.getElement();
        cell.setText(getText(command));
        cell.setImage(getImage(command));

        // highlight the currently active command
        if (active_command.get() == command)
            cell.setForeground(cell.getControl().getDisplay().getSystemColor(SWT.COLOR_GREEN));
        else
            cell.setForeground(null);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String getToolTipText(final Object element)
    {
        // Show base of command class and address
        final ScanCommand command = (ScanCommand) element;
        return command.getCommandName() + " @ " + command.getAddress();
    }
}
