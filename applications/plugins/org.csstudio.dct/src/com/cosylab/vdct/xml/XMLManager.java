package com.cosylab.vdct.xml;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
* Insert the class' description here.
* Creation date: (6.12.2001 21:54:33)
* @author 
*/
public final class XMLManager
{
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:54:51)
 * @param
 * @return
 */
public static void addDataNodeTo(Document doc, Element parentNode, String newNodeName, String newNodeData)
{
	Element node = (Element)doc.createElement(newNodeName);

	node.appendChild(doc.createTextNode(newNodeData));
	parentNode.appendChild(node);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:54:51)
 * @param
 * @return
 */
public static Node findNode(Node node, String name)
{
	if(node.getNodeName().equals(name))
		return node;

	if (node.hasChildNodes())
	{
		NodeList list = node.getChildNodes();
		int size = list.getLength();

		for (int i = 0; i < size; i++)
		{
			Node found = findNode(list.item(i), name);
			if (found!=null)
				return found;
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 21:56:40)
 * @param
 * @return
 */
private static DocumentBuilder getDocumentBuilder(final String dtdSymbol, final URL dtdUrl)
{

	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	if (dtdSymbol!=null && dtdUrl!=null)
		docBuilderFactory.setValidating(true);

	DocumentBuilder docBuilder = null;

	try
	{
		docBuilder = docBuilderFactory.newDocumentBuilder();
	}
	catch (Exception e)
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while trying to create new XML document builder!");
		com.cosylab.vdct.Console.getInstance().println(e.toString());
		return null;
	}

	docBuilder.setEntityResolver(
		
		new EntityResolver() {
			
			public InputSource resolveEntity(String publicId, String systemId)
			{
				if (dtdSymbol!=null && systemId.endsWith(dtdSymbol))
				{
					// Replacing systemId with dtdUrl
					try
					{
						Reader reader = new InputStreamReader(dtdUrl.openStream());
						return new InputSource(reader);
					}
					catch( Exception e )
					{
						com.cosylab.vdct.Console.getInstance().println("An error occured while trying to resolve the main DTD!");
						com.cosylab.vdct.Console.getInstance().println(e);
						return null;
					}
				}
				else
					return null;
			}
		}
	);

	return docBuilder;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:00:32)
 * @param
 * @return
 */
public static String getNodeAttribute(Node node, String name)
{
	if (node instanceof Element)
		return ((Element)node).getAttribute(name);
		
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:00:48)
 * @param
 * @return
 */
public static String getNodeValue(Node node)
{
	if (node.getFirstChild()==null)
		return "";
	else
		return node.getFirstChild().getNodeValue();
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:01:39)
 * @param
 * @return
 */
public static Document newDocument()
{
	try
	{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	catch( Exception e )
	{
		com.cosylab.vdct.Console.getInstance().println("An error occured while trying to create new XML document!");
		com.cosylab.vdct.Console.getInstance().println(e);
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:02:24)
 * @param
 * @return
 */
public static Document readFileDocument(String filename) throws IOException, SAXException, ParserConfigurationException
{
	return readFileDocument(filename, null, null);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:02:24)
 * @param
 * @return
 */
public static Document readFileDocument(String fileName, String dtdSymbol, URL dtdUrl) throws IOException, SAXException, ParserConfigurationException
{
	return getDocumentBuilder(dtdSymbol, dtdUrl).parse(new File(fileName));
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:02)
 * @param
 * @return
 */
public static Document readResourceDocument(String resource) throws IOException, SAXException, ParserConfigurationException
{
	return readResourceDocument(resource, null, null);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:15)
 * @param
 * @return
 */
public static Document readResourceDocument(String resource, String dtdSymbol, URL dtdUrl) throws IOException, SAXException, ParserConfigurationException
{
	return getDocumentBuilder(dtdSymbol, dtdUrl).parse(resource);
}
/**
 * Insert the method's description here.
 * Creation date: (6.12.2001 22:03:47)
 * @param
 * @return
 */
public static void writeDocument(String fileName, Document doc, String publicId, String systemId, String dtd) throws IOException
{
	OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
	
	try
	{

		// Serialisation through Tranform.
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.METHOD, "xml");
		//serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		if (systemId != null)
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
		if (publicId != null)
		serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult); 
	}
	catch (Throwable th) {
		throw new RuntimeException("Transform exception.", th);
	}

	out.flush();
	out.close();
		
}
}
