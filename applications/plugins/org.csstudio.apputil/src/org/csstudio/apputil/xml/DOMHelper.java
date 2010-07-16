/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.xml;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Helpers for parsing a DOM NodeList. 
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DOMHelper
{ 
    /** Reads a string that contains XML markup as a DOM.
     * @param xml String with XML content.
     * @return Returns the root Element.
     * @throws Exception on error.
     */
    public static final Element parseXMLString(final String xml) throws Exception
    {
        Document doc;
        // Open the XML file, read as DOM
        try
        {
            DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Reader r = new StringReader(xml);
            InputSource s = new InputSource(r);
            doc = docBuilder.parse(s);
        }
        catch (SAXParseException err)
        {
            throw new Exception(
                    "Parse error in '" + xml
                    + "', line "  + err.getLineNumber ()
                    + ", uri " + err.getSystemId ()
                    + " " + err.getMessage ());
        }
        catch (SAXException e)
        {
            Exception x = e.getException ();
            throw (x == null) ? e : x;
        }
        doc.getDocumentElement ().normalize();
        return doc.getDocumentElement();
    }
    
    /** Look for Element node if given name.
     *  <p>
     *  Checks the node and its siblings.
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
     *  Will only go one level down, not search the whole tree.
     *  
     *  @param element Element where to start looking. May be null.
     *  @param name Name of sub-element to locate.
     *  
     *  @return Returns string that was found or empty string.
     */
    public static final String getSubelementString(
            final Element element, final String name)
    {
        if (element == null)
            return "";
        Node n = element.getFirstChild();
        n = findFirstElementNode(n, name);
        if (n != null)
        {
            Node text_node = n.getFirstChild();
            if (text_node == null)
                return "";
            return text_node.getNodeValue();
        }
        return "";
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

    
    /** Locate all sub-elements tagged 'name', return their values.
     * 
     *  Will only go one level down, not search the whole tree.
     *  
     *  @param element Element where to start looking.
     *  @param name Name of sub-element to locate.
     *  
     *  @return Returns String array or null if nothing found.
     */
    public static final String [] getSubelementStrings(
            final Element element, final String name)
    {
        ArrayList<String> values = null;
        if (element != null)
        {
            Element n = findFirstElementNode(element.getFirstChild(), name);
            while (n != null)
            {
                if (values == null)
                    values = new ArrayList<String>();
                values.add(n.getFirstChild().getNodeValue());
                n = findNextElementNode(n, name);
            }
        }
        if (values != null  &&  values.size() > 0)
        {
            String [] val_array = new String[values.size()];
            values.toArray(val_array);
            return val_array;
        }
        return null;
    }    
    
    /** Locate a sub-element tagged 'name', return its boolean value.
     * 
     *  Will only go one level down, not search the whole tree.
     *  
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *  
     *  @return Returns <code>true</code> if sub-element was "true".
     *          If nothing was found, the default is <code>false</code>.
     */
    public static final boolean getSubelementBoolean(
            final Element element, final String element_name)
    {
        return getSubelementBoolean(element, element_name, false);
    }

    /** Locate a sub-element tagged 'name', return its boolean value.
     * 
     *  Will only go one level down, not search the whole tree.
     *  
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *  @param default_value Default to use if tag's value is neither "true" nor "false".
     *  
     *  @return Returns <code>true</code> if sub-element was "true",
     *          <code>false</code> if it was "false", otherwise the default.
     */
    public static final boolean getSubelementBoolean(
            final Element element, final String element_name, final boolean default_value)
    {
        final String s = getSubelementString(element, element_name);
        if (s.equalsIgnoreCase("true"))
            return true;
        if (s.equalsIgnoreCase("false"))
            return false;
        return default_value;
    }

    /** Locate attribute tagged 'name', return its boolean value.
     * 
     *  @param element Element where to start looking.
     *  @param name Name of the attribute.
     *  
     *  @return Returns true if attribute was "true". Defaults to false.
     */
    public static final boolean getAttributeBoolean(
            final Element element, final String name)
    {
        String s = element.getAttribute(name);
        return s.equalsIgnoreCase("true");
    }
   
    /** Locate a sub-element tagged 'name', return its integer value.
     *
     *  Will only go one level down, not seach the whole tree.
     *
     *  @param element Element where to start looking. May be null.
     *  @param element_name Name of sub-element to locate.
     *
     *  @return Returns number found in the sub-element.
     *  @exception Exception when nothing found or parse error in number.
     */
    public static final int getSubelementInt(
            final Element element, final String element_name) throws Exception
    {
        String s = getSubelementString(element, element_name);
        if (s.length() < 1)
            throw new Exception("No number found for tag '" + element_name + "'");
        return Integer.parseInt(s);
    }

    /** Locate a sub-element tagged 'name', return its integer value.
    *
    *  Will only go one level down, not seach the whole tree.
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
       String s = getSubelementString(element, element_name);
       if (s.length() < 1)
           return default_value;
       return Integer.parseInt(s);
   }
    
    /** Locate a sub-element tagged 'name', return its double value.
    *
    *  Will only go one level down, not seach the whole tree.
    *
    *  @param element Element where to start looking. May be null.
    *  @param element_name Name of sub-element to locate.
    *
    *  @return Returns number found in the sub-element.
    *  @exception Exception when nothing found or parse error in number.
    */
   public static final double getSubelementDouble(
           final Element element, final String element_name) throws Exception
   {
       String s = getSubelementString(element, element_name);
       if (s.length() < 1)
           throw new Exception("No number found for tag '" + element_name + "'");
       return Double.parseDouble(s);
   }
   
   /** Locate a sub-element tagged 'name', return its double value.
   *
   *  Will only go one level down, not seach the whole tree.
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
      final String s = getSubelementString(element, element_name);
      if (s.length() < 1)
          return default_value;
      return Double.parseDouble(s);
  }

}
