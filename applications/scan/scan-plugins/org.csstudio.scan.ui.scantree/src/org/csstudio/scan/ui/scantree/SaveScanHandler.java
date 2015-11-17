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

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.server.ScanInfo;
import org.eclipse.core.resources.IFile;

/** Command handler that saves a downloaded scan as a file
 *  @author Kay Kasemir
 */
public class SaveScanHandler extends AbstractScanHandler
{
    @Override
    protected void handleScan(final ScanClient client, final ScanInfo info, final String xml_commands) throws Exception
    {
        final AtomicReference<IFile> file_ref = new AtomicReference<>();

        // Prompt for file name, which requires UI thread
        shell.getDisplay().syncExec(() ->
        {
            file_ref.set(ScanEditor.promptForFile(shell, null));
        });

        final IFile file = file_ref.get();
        if (file == null)
            return;

        // Write commands as XML to buffer
        // Write the buffer to file
        final ByteArrayInputStream stream = new ByteArrayInputStream(xml_commands.getBytes());
        if (file.exists())
            file.setContents(stream, IFile.FORCE, null);
        else
            file.create(stream, true, null);
    }
}
