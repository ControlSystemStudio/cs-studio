/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        final List<PVTableItem> items = new ArrayList<>();
        final IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
        if (sel != null  &&  sel.size() > 0)
        {	// Use selected model items
        	final Iterator<?> iterator = sel.iterator();
        	while (iterator.hasNext())
        		items.add((PVTableItem) iterator.next());
        }
        else
        {	// Use all model items
        	final int N = model.getItemCount();
        	for (int i=0; i<N; ++i)
        		items.add(model.getItem(i));
        }
        
        if (items.isEmpty())
        	return;
        
        double tolerance = items.get(0).getTolerance();
        
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
        for (PVTableItem item : items)
            item.setTolerance(tolerance);
        model.fireModelChange();
    }
}
