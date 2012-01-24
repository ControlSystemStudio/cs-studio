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
package org.csstudio.scan.ui.scantree.actions;

import java.util.List;

import org.csstudio.scan.client.ScanServerConnector;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

/** Action that opens a scan in the tree editor
 *  @author Kay Kasemir
 */
public class OpenScanTreeAction extends Action
{
    final private ScanInfo info;
    
    /** Initialize
     *  @param model
     *  @param info
     */
    public OpenScanTreeAction(final ScanInfo info)
    {
        super(Messages.OpenScanTree, Activator.getImageDescriptor("icons/scantree.gif")); //$NON-NLS-1$
        this.info = info;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        // Use Job to submit?
        try
        {
            // Fetch commands from server
            final ScanServer server = ScanServerConnector.connect();
            final String xml_commands = server.getScanCommands(info.getId());
            final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
            final List<ScanCommand> commands = reader.readXMLString(xml_commands);
            ScanServerConnector.disconnect(server);
            // Open in editor
            final ScanEditor editor = ScanEditor.createInstance();
            editor.setCommands(commands);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(null, Messages.Error,
                NLS.bind(Messages.OpenScanTreeErrorFmt, ex.getMessage()));
        }
    }
}
