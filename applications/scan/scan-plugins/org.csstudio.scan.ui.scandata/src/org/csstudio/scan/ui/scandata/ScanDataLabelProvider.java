/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.data.ScanSample;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/** Table cell label provider for cells that display
 *  a {@link ScanSample} from a {@link ScanDataRow}
 *  @author Kay Kasemir
 */
public class ScanDataLabelProvider extends CellLabelProvider
{
	final private int index;

	/** Initialize
	 *  @param index Index of device within {@link ScanDataRow}
	 */
	public ScanDataLabelProvider(final int index)
    {
		this.index = index;
    }

	@Override
    public String getToolTipText(Object element)
    {
	    final ScanDataRow row = (ScanDataRow) element;
		final ScanSample sample = row.getSample(index);
		if (sample == null)
			return Messages.NoSampleTT;
		else
			return sample.toString();
    }

	@Override
	public void update(final ViewerCell cell)
	{
	    final ScanDataRow row = (ScanDataRow) cell.getElement();
		final ScanSample sample = row.getSample(index);
		cell.setText(ScanSampleFormatter.asString(sample));
	}
}
