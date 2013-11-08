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

/** Command that adds comment to a scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommentCommand extends ScanCommand
{
    private volatile String comment;

    /** Initialize with example comment */
    public CommentCommand()
    {
        this("This is a comment");
    }

    /** Initialize
     *  @param comment Comment
     */
    public CommentCommand(final String comment)
    {
        this.comment = comment;
    }

    /** {@inheritDoc} */
    @Override
    protected void configureProperties(final List<ScanCommandProperty> properties)
    {
        properties.add(new ScanCommandProperty("comment", "Comment", String.class));
        // NOT calling super.configureProperties(properties);
    }

    /** @return Comment */
    public String getComment()
    {
        return comment;
    }

    /** @param comment Desired comment */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        writeIndent(out, level);
        out.println("<comment><text>" + comment + "</text></comment>");
        // NOT calling super.writeXML(out, level);
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception
    {
        setComment(DOMHelper.getSubelementString(element, "text", ""));
        // NOT calling super.readXML(factory, element);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        if (comment == null  ||  comment.isEmpty())
            return "-- Empty Comment --";
        return comment;
    }
}
