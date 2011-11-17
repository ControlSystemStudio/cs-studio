/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor.actions;

import java.io.StringWriter;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action that fetches scan data
 *  @author Kay Kasemir
 */
public class GetScanDataAction extends AbstractGUIAction
{
    /** Initialize
     *  @param model
     *  @param info
     */
    public GetScanDataAction(final ScanInfoModel model, final ScanInfo info)
    {
        super(model, info, "Get Data", "icons/information.gif");
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        final ScanData data = model.getScanData(info);
        
        // TODO Display data in separate view (plot)
        final StringWriter buf = new StringWriter();
        buf.append("Data for ").append(info.toString()).append(":\n");
        if (data == null)
            buf.append(" - nothing -");
        else
            new SpreadsheetScanDataIterator(data).dump(buf);
        
        MessageDialog.openInformation(null, "Scan Data", buf.toString());
    }
}
