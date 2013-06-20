/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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
import java.util.List;

import org.w3c.dom.Element;

/** Command that includes existing *.scn file with macros
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class IncludeCommand extends ScanCommand
{
    private volatile String scan_file;
    private volatile String macros;

    /** Initialize with example values */
    public IncludeCommand()
    {
        this("other.scn", "macro=value");
    }

    /** Initialize
     *  @param comment Comment
     */
    public IncludeCommand(final String scan_file, final String macros)
    {
        this.scan_file = scan_file;
        this.macros = macros;
    }

    /** {@inheritDoc} */
    @Override
    protected void configureProperties(final List<ScanCommandProperty> properties)
    {
        properties.add(new ScanCommandProperty("scan_file", "Scan File", String.class));
        properties.add(new ScanCommandProperty("macros", "Macros", String.class));
        super.configureProperties(properties);
    }

    /** @return Scan File */
    public String getScanFile()
    {
        return scan_file;
    }

    /** @param scan_file Desired scan file */
    public void setScanFile(final String scan_file)
    {
        this.scan_file = scan_file;
    }

    /** @return Macros */
    public String getMacros()
    {
        return macros;
    }

    /** @param macros Macros */
    public void setMacros(final String macros)
    {
        this.macros = macros;
    }
    
    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<include>");
        writeIndent(out, level+1);
        out.println("<scan_file>" + getScanFile() + "</scan_file>");
        writeIndent(out, level+1);
        out.println("<macros>" + getMacros() + "</macros>");
        super.writeXML(out, level);
        writeIndent(out, level);
        out.println("</include>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setScanFile(DOMHelper.getSubelementString(element, "scan_file", ""));
        setMacros(DOMHelper.getSubelementString(element, "macros", ""));
        super.readXML(factory, element);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Include '").append(getScanFile()).append("'");
        if (! getMacros().isEmpty())
            buf.append(", ").append(getMacros());
        return buf.toString();
    }
}
