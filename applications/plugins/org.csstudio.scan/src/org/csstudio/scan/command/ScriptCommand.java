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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Command that executes a script
 *
 *  <p>The script must implement the {@link ScanScript} interface.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptCommand extends ScanCommand
{
    private volatile String script;
    private volatile String[] args;

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
        this(script, new String[0]);
    }

    /** Initialize
     *  @param script Script
     *  @param args Arguments
     */
    public ScriptCommand(final String script, final String[] args)
    {
        this.script = script;
        this.args = args;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void configureProperties(final List<ScanCommandProperty> properties)
    {
        properties.add(new ScanCommandProperty("script", "Script", String.class));
        properties.add(new ScanCommandProperty("arguments", "Arguments", String[].class));
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

    /** @return Script arguments */
    public String[] getArguments()
    {
        return Arrays.copyOf(args, args.length);
    }

    /** @param args Script arguments */
    public void setArguments(final String... args)
    {
        this.args = args;
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
        writeIndent(out, level+1);
        out.println("<arguments>");
        for (String arg : args)
        {
            writeIndent(out, level+2);
            out.println("<argument>" + arg + "</argument>");
        }
        writeIndent(out, level+1);
        out.println("</arguments>");
        super.writeXML(out, level);
        writeIndent(out, level);
        out.println("</script>");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setScript(DOMHelper.getSubelementString(element, "path", ""));
        
        final List<String> arguments = new ArrayList<String>();
        Element node = DOMHelper.findFirstElementNode(element.getFirstChild(), "arguments");
        if (node != null)
        {
            node = DOMHelper.findFirstElementNode(node.getFirstChild(), "argument");
            while (node != null)
            {
                Node text_node = node.getFirstChild();
                if (text_node == null)
                    throw new Exception("Missing argument");
                arguments.add(text_node.getNodeValue());
                node = DOMHelper.findNextElementNode(node, "argument");
            }
            setArguments(arguments.toArray(new String[arguments.size()]));
        }
        
        super.readXML(factory, element);
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("Script ");
		buf.append("'").append(script).append("'");
		
		boolean first = true;
		for (String arg : args)
		{
		    if (first)
		    {
		        buf.append(" ('").append(arg).append("'");
		        first = false;
		    }
		    else
                buf.append(", '").append(arg).append("'");
		}
		if (! first)
		    buf.append(")");
		
	    return buf.toString();
	}
}
