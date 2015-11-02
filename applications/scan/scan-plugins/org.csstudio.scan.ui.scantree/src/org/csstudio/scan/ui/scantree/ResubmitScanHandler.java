/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.server.ScanInfo;

/** Command handler that fetches a scan from server and re-submits it
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ResubmitScanHandler extends AbstractScanHandler
{
    @Override
    protected void handleScan(final ScanClient client, final ScanInfo info, final String xml_commands) throws Exception
    {
        client.submitScan(info.getName(), xml_commands, true);
    }
}
