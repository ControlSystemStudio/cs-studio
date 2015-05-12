/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.model.Model;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** A (lazy) content provider for the GUI's table.
 *  <p>
 *  TableViewer calls this, asking for updates to the "rows" of the
 *  SWT Table, and we respond with Instances of the Model.
 *
 *  @author Kay Kasemir
 *
 *     reviewed by Delphy 01/29/09
 */
public class ModelInstanceProvider implements ILazyContentProvider
{
    private TableViewer table_viewer;
    private Model model;

    /** We happen to know that this is called by the GUI via
     *  TableViewer.setInput(Model).
     *  It's also called with a <code>null</code> input
     *  when the application shuts down.
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        table_viewer = (TableViewer) viewer;
        model = (Model)newInput;

        // Setting the item count causes a 'refresh' of the table
        if (model == null)
            table_viewer.setItemCount(0);
        else
            table_viewer.setItemCount(model.getInstanceCount());
    }

    /** Called by viewer; we have to update the given row of the TableViewer
     *  with the corresponding Model element (Instance)
     *  @param row Row to update
     */
    @Override
    public void updateElement(final int row)
    {
        table_viewer.replace(model.getInstance(row), row);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // Nothing to dispose
    }
}
