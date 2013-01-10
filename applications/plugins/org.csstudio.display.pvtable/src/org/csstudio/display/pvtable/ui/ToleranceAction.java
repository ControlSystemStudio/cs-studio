/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.Iterator;

import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;

/** {@link Action} to save value snapshots
 *  @author Kay Kasemir
 */
public class ToleranceAction extends PVTableAction
{
    public ToleranceAction(final TableViewer viewer)
    {
        super(Messages.Tolerance, "icons/pvtable.png", viewer); //$NON-NLS-1$
        setToolTipText(Messages.Tolerance_TT);
    }
    
    public void run()
    {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model == null)
            return;
        final IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
        if (sel == null  ||  sel.size() <= 0)
            return;
        
        PVTableItem item = (PVTableItem) sel.getFirstElement();
        double tolerance = item.getTolerance();
        
        final InputDialog dlg = new InputDialog(viewer.getControl().getShell(),
                Messages.Tolerance, Messages.EnterTolerance,
                Double.toString(tolerance),
                new IInputValidator()
                {
                    @Override
                    public String isValid(final String text)
                    {
                        try
                        {
                            double x = Double.parseDouble(text);
                            if (x >= 0.0)
                                return null;
                        }
                        catch (NumberFormatException ex)
                        {
                            // Ignore, fall through
                        }
                        return Messages.EnterPositiveTolerance;
                    }
                });
        
        if (dlg.open() != Window.OK)
            return;
        
        tolerance = Double.parseDouble(dlg.getValue());
        final Iterator<?> iterator = sel.iterator();
        while (iterator.hasNext())
        {
            item = (PVTableItem) iterator.next();
            item.setTolerance(tolerance);
        }
    }
}
