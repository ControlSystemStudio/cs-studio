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
 * 
 *  <p>Selectable options are strings.
 *  The scan ID that's used to identify a specific
 *  scan is encoded/decoded in the string.
 *  
 *  @author Kay Kasemir
 */
public class ScanSelectorAction extends DropdownToolbarAction
{
    /** Scan model */
    final private PlotDataModel model;
    
    /** Plot */
    final private Plot plot;

    /** Initialize 
     * @param model */
    public ScanSelectorAction(final PlotDataModel model, final Plot plot)
    {
        super(Messages.Scan, Messages.Scan_TT);
        this.model = model;
        this.plot = plot;
    }

    /** @param scan ScanInfo
     *  @return String for drop-down entry that encodes the scan
     */
    public static String encode(final ScanInfo scan)
    {
        return encode(scan.getName(), scan.getId());
    }

    /** @param name Scan name
     *  @param id Scan ID
     *  @return String for drop-down entry that encodes the scan
     */
    @SuppressWarnings("nls")
    public static String encode(final String name, final long id)
    {
        return name + " [" + id + "]";
    }
    
    /** @param option Option in drop-down list
     *  @return Decoded Scan ID or <code>-1</code>
     */
    public static long decode(final String option)
    {
        final int sep = option.lastIndexOf('[');
        if (sep <= 0)
            return -1;
        final int len = option.length();
        try
        {
            return Integer.parseInt(option.substring(sep + 1, len-1));
        }
        catch (NumberFormatException ex)
        {
            return -1;
        }
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
            scans[i] = encode(info);
        }
        return scans;
    }

    /** {@inheritDoc} */
    @Override
    public void handleSelection(final String item)
    {
        // Parse scan ID out of "Some Scan Name [42]"
        final long id = decode(item);
        if (id >= 0)
        {
            model.selectScan(id);
            plot.setTitle(item);
        }
    }
}
