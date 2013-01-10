/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Launch configuration
 * 
 *  <p>Can parse the launch config XML file
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LaunchConfig
{
	private String command;
	private String icon_name;

	/** Initialize empty launch configuration */
    public LaunchConfig()
    {
    	command = "";
    	icon_name = "icon:run";
    }
	
	/** Initialize
	 *  @param file {@link File} with launch config 
	 *  @throws Exception on error in launch config
	 */
	public LaunchConfig(final File file) throws Exception
    {
		this(new FileInputStream(file));
    }

	/** Initialize
	 *  @param stream {@link InputStream} with launch config 
	 *  @throws Exception on error in launch config
	 */
	public LaunchConfig(final InputStream stream) throws Exception
    {
		this(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream));
    }
	
	/** Initialize
	 *  @param doc {@link Document} with launch config
	 *  @throws Exception on error in config doc
	 */
	public LaunchConfig(final Document doc) throws Exception
    {
        doc.getDocumentElement().normalize();
        // Check if it's an <application/>.
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        if (!root_node.getNodeName().equals("application"))
            throw new Exception("Expecting <application>");
        
        command = DOMHelper.getSubelementString(root_node, "command");
        if (command.isEmpty())
        	throw new Exception("Missing <command>");
        icon_name = DOMHelper.getSubelementString(root_node, "icon");
    }

	/** @param command Command */
	public void setCommand(final String command)
    {
    	this.command = command;
    }

	/** @return Command */
	public String getCommand()
    {
    	return command;
    }

	/** @param icon_name Icon name */
    public void setIconName(final String icon_name)
    {
    	this.icon_name = icon_name;
    }

	/** @return Icon name */
	public String getIconName()
    {
    	return icon_name;
    }

	/** Obtain image for a build-in icon
	 *  @param icon_name Basic icon name like "run"
	 *  @return {@link Image}, to be disposed by caller
	 */
	public static Image getBuildinIcon(final String icon_name)
	{
		final ImageDescriptor descr = Activator.getImageDescriptor("icons/" + icon_name + ".gif");
		if (descr == null)
			return null;
		return descr.createImage();
	}
	
	public String getXML()
	{
		return
		"<application>\n" +
		"  <command>" + command + "</command>\n" +
		"  <icon>" + icon_name + "</icon>\n" +
		"</application>\n";
	}
}
