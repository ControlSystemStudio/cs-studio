/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** XML Parsingutil
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLUtil
{
    /** Look for Element node if given name.
     *  <p>
     *  Checks the node and its siblings.
     *  Does not descent down the 'child' links.
     *  @param node Node where to start.
     *  @param name Name of the nodes to look for.
     *  @return Returns node or the next matching sibling or null.
     */
    final public static Element findFirstElementNode(Node node, final String name)
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

    /** Locate a sub-element tagged 'name', return its value.
     *
     *  Will only go one level down, not search the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param name Name of sub-element to locate.
     *  @param default_value Default value if not found
     *
     *  @return Returns string that was found or default_value.
     */
    final public static String getSubelementString(
            final Element element, final String name, final String default_value)
    {
        if (element == null)
            return default_value;
        Node n = element.getFirstChild();
        n = findFirstElementNode(n, name);
        return getString(n, default_value);
    }

    final public static String getString(final Node node, final String default_value)
    {
        if (node != null)
        {
            Node text_node = node.getFirstChild();
            if (text_node == null)
                return default_value;
            return text_node.getNodeValue();
        }
        return default_value;
    }

    /** Locate a sub-element tagged 'name', return its value.
     *
     *  @param element Element where to start looking. May be null.
     *  @param name Name of sub-element to locate.
     *
     *  @return Returns string that was found or NaN
     *  @throws Exception on error in number format
     */
    final public static double getSubelementDouble(
            final Element element, final String name) throws Exception
    {
        return getSubelementDouble(element, name, Double.NaN);
    }

    /** Locate a sub-element tagged 'name', return its value.
    *
    *  @param element Element where to start looking. May be null.
    *  @param name Name of sub-element to locate.
    *  @param default_value Default value if not found
    *
    *  @return Returns double string that was found
    *  @throws Exception on error in number format
    */
    final public static double getSubelementDouble(
           final Element element, final String name, final double default_value) throws Exception
    {
        final String text = getSubelementString(element, name, "").trim();
        if (text.isEmpty())
            return default_value;
        try
        {
            return Double.parseDouble(text);
        }
        catch (NumberFormatException ex)
        {
            throw new Exception("Invalid number for <" + name + ">", ex);
        }
    }
}
