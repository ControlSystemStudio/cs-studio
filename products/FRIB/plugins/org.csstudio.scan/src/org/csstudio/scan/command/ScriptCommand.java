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
import java.util.List;

import org.w3c.dom.Element;

/** Command that executes a script
 *
 *  <p>The script must implement the {@link ScanScript} interface.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptCommand extends ScanCommand
{
    private volatile String script;

    /** Initialize empty script command */
    public ScriptCommand()
    {
        this("the_script.py");
    }

	/** Initialize
     *  @param script Script
     */
    public ScriptCommand(final String script)
    {
        this.script = script;
    }

    /** {@inheritDoc} */
    @Override
    protected void configureProperties(final List<ScanCommandProperty> properties)
    {
        properties.add(new ScanCommandProperty("script", "Script", String.class));
        super.configureProperties(properties);
    }

	/** @return Name of script class */
    public String getScript()
    {
        return script;
    }

    /** @param script Name of the script class */
    public void setScript(final String script)
    {
        this.script = script;
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<script>");
        writeIndent(out, level+1);
        out.print("<path>");
        out.print(script);
        out.println("</path>");
        super.writeXML(out, level);
        writeIndent(out, level);
        out.println("</script>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setScript(DOMHelper.getSubelementString(element, "path", ""));
        super.readXML(factory, element);
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("Script ");
		buf.append("'").append(script).append("'");
	    return buf.toString();
	}
}
