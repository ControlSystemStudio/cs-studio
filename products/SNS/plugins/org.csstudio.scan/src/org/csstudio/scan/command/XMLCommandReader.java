/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Read {@link ScanCommand}s from XML stream
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLCommandReader
{
    final private InputStream in;

    /** Initialize
     *  @param in Where to read the commands
     */
    public XMLCommandReader(final InputStream in)
    {
        this.in = in;
    }

    public List<ScanCommand> readXML() throws Exception
    {
        final DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = docBuilder.parse(in);
        return readDocument(doc);
    }

    public List<ScanCommand> readDocument(final Document doc) throws Exception
    {
        doc.getDocumentElement().normalize();

        final Element root_node = doc.getDocumentElement();
        if (! "commands".equals(root_node.getNodeName()))
            throw new Exception("Wrong document type");
        
        Node node = root_node.getFirstChild();
        return readCommands(node);
    }

    public static List<ScanCommand> readCommands(Node node) throws Exception
    {
        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
                commands.add(readCommand((Element) node));
            node = node.getNextSibling();
        }
        return commands;
    }

    public static ScanCommand readCommand(final Element element) throws Exception
    {
        final String type = element.getNodeName();
        if ("set".equals(type))
            return SetCommand.fromXML(element);
        else if ("wait".equals(type))
            return WaitForValueCommand.fromXML(element);
        else if ("loop".equals(type))
            return LoopCommand.fromXML(element);
        else if ("log".equals(type))
            return LogCommand.fromXML(element);
        else if ("delay".equals(type))
            return DelayCommand.fromXML(element);
        throw new Exception("Unknown command type '" + type + "'");
    }
}
