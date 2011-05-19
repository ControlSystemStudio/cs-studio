/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.sampleview;

import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide PlotSample items for table with type SWT.VIRTUAL.
 *  TableViewerInput is a ModelItem
 *  @author Kay Kasemir
 */
public class SampleTableContentProvider implements ILazyContentProvider
{
    private TableViewer sample_table;
    private PlotSamples samples;

    /** Called by table when its 'input' is changed */
    @Override
    public void inputChanged(final Viewer viewer, final Object old, final Object model_item)
    {
        sample_table = (TableViewer) viewer;
        if (model_item == null)
        {
            samples = null;
            sample_table.setItemCount(0);
            return;
        }
        samples = ((ModelItem)model_item).getSamples();
        synchronized (samples)
        {
            sample_table.setItemCount(samples.getSize());
        }
    }

    /** Called by 'lazy' table, needs to 'replace' entry of given row. */
    @Override
    public void updateElement(final int row)
    {
        synchronized (samples)
        {
            sample_table.replace(samples.getSample(row), row);
        }
    }

    @Override
    public void dispose()
    {   /* NOP */ }
}
