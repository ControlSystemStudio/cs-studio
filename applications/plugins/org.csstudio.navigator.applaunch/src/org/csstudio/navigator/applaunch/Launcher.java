/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorLauncher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Launcher for an (external) application
 * 
 *  <p>plugin.xml associates this with application files.
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Launcher implements IEditorLauncher
{
	/** Invoked by Eclipse with the path to the launcher config
	 *  {@inheritDoc}
	 */
	@Override
	public void open(final IPath path)
	{
		try
		{
			launchConfig(path.toFile());
		}
		catch (Exception ex)
		{
			MessageDialog.openError(null,
					Messages.Error,
					NLS.bind(Messages.ConfigFileErrorFmt,
							path, ex.getMessage()));
		}
	}

	/** Launch program
	 *  @param file {@link File} with launch config
	 *  @throws Exception on error in config file
	 */
	private void launchConfig(final File file) throws Exception
    {
		final DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		launchConfig(builder.parse(file));
    }

	/** Launch program
	 *  @param doc {@link Document} with launch config
	 *  @throws Exception on error in config doc
	 */
    private void launchConfig(final Document doc) throws Exception
    {
        doc.getDocumentElement().normalize();
        // Check if it's an <application/>.
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        if (!root_node.getNodeName().equals("application"))
            throw new Exception("Expecting <application>");
        
        final String command =
        	DOMHelper.getSubelementString(root_node, "command");
        if (command.isEmpty())
        	throw new Exception("Missing <command>");
        
        launchCommand(command);
    }

	/** Execute a command
	 *  @param command
	 */
	private void launchCommand(final String command)
    {
		// Is that really all?
        Program.launch(command);
    }
}
