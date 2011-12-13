/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Read {@link ScanCommand}s from XML stream.
 * 
 *  <p>Depends on each command to be in the
 *  package <code>org.csstudio.scan.command</code>
 *  and to provide a method
 *  <code>public static ScanCommand fromXML(Element element)</code>
 *  to allow creation from XML.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLCommandReader
{
    /** Read scan commands from XML stream
     *  @param in XML stream that contain commands
     *  @return List of {@link ScanCommand}s
     *  @throws Exception on error: Unknown commands, missing command-specific details
     */
    public static List<ScanCommand> readXMLStream(final InputStream in) throws Exception
    {
        final DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = docBuilder.parse(in);
        return readDocument(doc);
    }

    /** Read scan commands from XML document
     *  @param doc Document that contain commands
     *  @return List of {@link ScanCommand}s
     *  @throws Exception on error: Unknown commands, missing command-specific details
     */
    public static List<ScanCommand> readDocument(final Document doc) throws Exception
    {
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        if (! "commands".equals(root_node.getNodeName()))
            throw new Exception("Wrong document type");
        return readCommands(root_node.getFirstChild());
    }

    /** Read a list of commands (and possible sub-commands like loop bodies)
     *  @param node Node and siblings that contain commands
     *  @return List of {@link ScanCommand}s
     *  @throws Exception on error: Unknown commands, missing command-specific details
     */
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

    /** Read a ScanCommand from an XML element.
     * 
     *  @param element XML element. Name of the element determines the ScanCommand
     *  @return {@link ScanCommand}
     *  @throws Exception on error: Unknown command, missing command-specific detail
     */
    public static ScanCommand readCommand(final Element element) throws Exception
    {
        final String type = element.getNodeName();
        try
        {
            // Turn type "set" into "....SetCommand"
            final String classname = "org.csstudio.scan.command." +
                    type.substring(0, 1).toUpperCase() +
                    type.substring(1).toLowerCase() + "Command";
            final Class<?> command_class = Class.forName(classname);
            // Try to invoke static ScanCommand fromXML(Element)
            final Method method = command_class.getMethod("fromXML", Element.class);
            return (ScanCommand) method.invoke(null, element);
        }
        catch (Throwable ex)
        {
            throw new Exception("Unknown command type '" + type + "'");
        }
    }
}
