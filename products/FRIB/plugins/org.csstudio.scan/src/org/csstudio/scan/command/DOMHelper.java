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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Helper for parsing XML document
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DOMHelper
{
    /** Look for Element node of given name.
     *
     *  <p>Checks the node and its siblings.
     *  Does not descent down the 'child' links.
     *  @param node Node where to start.
     *  @param name Name of the nodes to look for.
     *  @return Returns node or the next matching sibling or null.
     */
    public static final Element findFirstElementNode(Node node, final String name)
    {
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals(name))
                return (Element) node;
            node = node.getNextSibling();
        }
        return null;
    }

    /** @return Returns the next matching element.
     *  @see #findFirstElementNode(Node, String)
     */
    public static final Element findNextElementNode(final Node node, final String name)
    {
        return findFirstElementNode(node.getNextSibling(), name);
    }

    /** Locate a sub-element tagged 'name', return its value.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param name Name of sub-element to locate.
     *  @param default_value Default value if not found
     *
     *  @return Returns string that was found or default_value.
     */
    public static final String getSubelementString(
            final Element element, final String name, final String default_value)
    {
        if (element == null)
            return default_value;
        Node n = element.getFirstChild();
        n = findFirstElementNode(n, name);
        if (n != null)
        {
            Node text_node = n.getFirstChild();
            if (text_node == null)
                return default_value;
            return text_node.getNodeValue();
        }
        return default_value;
    }

    /** Locate a sub-element tagged 'name', return its value.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking.
     *  @param name Name of sub-element to locate.
     *
     *  @return Returns string that was found
     *  @throws Exception when nothing found
     */
    public static final String getSubelementString(final Element element, final String name) throws Exception
    {
        if (element == null)
            throw new Exception("Missing '" + name + "'");
        Node n = element.getFirstChild();
        n = findFirstElementNode(n, name);
        if (n != null)
        {
            Node text_node = n.getFirstChild();
            if (text_node != null)
                return text_node.getNodeValue();
        }
        throw new Exception("Missing '" + name + "'");
    }

    /** Locate a sub-element tagged 'name', return its double value.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking.
     *  @param element_name Name of sub-element to locate.
     *
     *  @return Returns number found in the sub-element.
     *  @throws Exception when nothing found
     */
    public static final double getSubelementDouble(
          final Element element, final String element_name) throws Exception
    {
        final String s = getSubelementString(element, element_name);
        try
        {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException ex)
        {
            throw new Exception("Missing numeric value for '" + element_name + "'");
        }
    }

    /** Locate a sub-element tagged 'name', return its double value.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *  @param default_value Result in case sub-element isn't found.
     *
     *  @return Returns number found in the sub-element.
     *  @throws Exception when nothing found
     */
    public static final double getSubelementDouble(
           final Element element, final String element_name, final double default_value)  throws Exception
    {
        final String s = getSubelementString(element, element_name, "");
        if (s.length() < 1)
            return default_value;
        try
        {
            return Double.parseDouble(s);
        }
        catch (NumberFormatException ex)
        {
            throw new Exception("Missing numeric value for '" + element_name + "'");
        }
    }

    /** Locate a sub-element tagged 'name', return its value as String if it's quoted
     *  "like a string", or as a number if it's just a number.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *
     *  @return Returns number or string (quotes removed) found in the sub-element.
     *  @throws Exception when nothing found
     */
    public static final Object getSubelementStringOrDouble(
          final Element element, final String element_name) throws Exception
    {
        final String text = getSubelementString(element, element_name, "");
        if (text.length() < 1)
            throw new Exception("Missing value for '" + element_name + "'");
        if (text.startsWith("\"")  &&  text.endsWith("\""))
            return text.substring(1, text.length()-1);
        try
        {
            return Double.parseDouble(text);
        }
        catch (NumberFormatException ex)
        {
            // String should be quoted, non-string should be a number,
            // but older clients might send unquoted text, so
            // treat non-parsable number as text.
            Logger.getLogger(DOMHelper.class.getName())
                .log(Level.WARNING, "Expected numeric value for <" + element_name + ">, treating as string \"" + text + "\"");
            return text;
        }
    }
    
    /** Locate a sub-element tagged 'name', return its integer value.
     *
     *  <p>Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *  @param default_value Result in case sub-element isn't found.
     *
     *  @return Returns number found in the sub-element.
     *  @throws Exception when nothing found
     */
    public static final int getSubelementInt(
          final Element element, final String element_name, final int default_value)  throws Exception
    {
        return (int) getSubelementLong(element, element_name, default_value);
    }

    /** Locate a sub-element tagged 'name', return its long integer value.
    *
    *  <p>Will only go one level down, not search the whole tree.
    *
    *  @param element Element where to start looking. May be null.
    *  @param element_name Name of sub-element to locate.
    *  @param default_value Result in case sub-element isn't found.
    *
    *  @return Returns number found in the sub-element.
    *  @throws Exception when nothing found
    */
   public static final long getSubelementLong(
         final Element element, final String element_name, final long default_value)  throws Exception
   {
       final String s = getSubelementString(element, element_name, "");
       if (s.length() < 1)
           return default_value;
       try
       {
           return Long.parseLong(s);
       }
       catch (NumberFormatException ex)
       {
           throw new Exception("Missing numeric value for '" + element_name + "'");
       }
   }
}
