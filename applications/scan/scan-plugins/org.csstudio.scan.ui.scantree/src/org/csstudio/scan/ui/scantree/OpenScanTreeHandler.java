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
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.server.ScanInfo;

/** Command handler that opens a scan in the tree editor
 *  @author Kay Kasemir
 */
public class OpenScanTreeHandler extends AbstractScanHandler
{
    @Override
    protected void handleScan(final ScanClient client, final ScanInfo info, final String xml_commands) throws Exception
    {
        final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
        final List<ScanCommand> commands = reader.readXMLString(xml_commands);

        // Open in editor, which requires UI thread
        shell.getDisplay().asyncExec(() ->  ScanEditor.createInstance(info, commands));
    }
}
