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
     */
    public static final double getSubelementDouble(
           final Element element, final String element_name, final double default_value)
    {
        final String s = getSubelementString(element, element_name, "");
        if (s.length() < 1)
            return default_value;
        return Double.parseDouble(s);
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
    */
   public static final int getSubelementInt(
          final Element element, final String element_name, final int default_value)
   {
       final String s = getSubelementString(element, element_name, "");
       if (s.length() < 1)
           return default_value;
       return Integer.parseInt(s);
   }

}
