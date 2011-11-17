/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.List;

import org.csstudio.apputil.ui.swt.DropdownToolbarAction;
import org.csstudio.scan.server.ScanInfo;

/** Toolbar action to select the scan
 *  @author Kay Kasemir
 */
public class ScanSelectorAction extends DropdownToolbarAction
{
    /** Separator between scan ID and name */
    private static final String SEPARATOR = " - ";
    
    /** Scan model */
    final private PlotDataModel model;

    /** Initialize 
     * @param model */
    public ScanSelectorAction(final PlotDataModel model)
    {
        super("Scan", "Select a Scan");
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        final List<ScanInfo> infos = model.getScanInfos();
        final String[] scans = new String[infos.size()];
        for (int i=0; i<scans.length; ++i)
        {
            final ScanInfo info = infos.get(i);
            scans[i] = info.getId() + SEPARATOR + info.getName();
        }
        return scans;
    }

    /** {@inheritDoc} */
    @Override
    public void handleSelection(final String item)
    {
        // Parse scan ID out of "42 - Some Scan Name"
        final int sep = item.indexOf(SEPARATOR);
        if (sep <= 0)
            return;
        final int id;
        try
        {
            id = Integer.parseInt(item.substring(0, sep));
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        model.selectScan(id);
    }
}
