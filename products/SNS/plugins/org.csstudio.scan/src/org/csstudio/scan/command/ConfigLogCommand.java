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
package org.csstudio.scan.command;

import java.io.PrintStream;

import org.w3c.dom.Element;

/** Command that configures logging
 *
 *  <p>Since scan tree editor presents commands in palette ordered by
 *  their class name, all commands that configure something should
 *  start with "Config..."
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConfigLogCommand extends ScanCommand
{
    /** Configurable properties of this command */
    final private static ScanCommandProperty[] properties = new ScanCommandProperty[]
    {
    	new ScanCommandProperty("automatic", "Log automatically", Boolean.class)
    };

    private volatile boolean automatic = false;

    /** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return properties;
    }

    /** @return Log automatically? */
    public boolean getAutomatic()
    {
        return automatic;
    }

    /** @param automatic Use automatic logging? */
    public void setAutomatic(final Boolean automatic)
    {
        this.automatic = automatic;
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<config_log>");
        if (automatic)
        {
            writeIndent(out, level+1);
            out.println("<automatic>" + automatic + "</automatic>");
        }
        writeIndent(out, level);
        out.println("</config_log>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setAddress(DOMHelper.getSubelementInt(element, ScanCommandProperty.TAG_ADDRESS, -1));
        setAutomatic(Boolean.parseBoolean(DOMHelper.getSubelementString(element, "automatic", "false")));
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
		if (automatic)
			return "Log mode: automatically";
		else
			return "Log mode: on demand";
	}
}
