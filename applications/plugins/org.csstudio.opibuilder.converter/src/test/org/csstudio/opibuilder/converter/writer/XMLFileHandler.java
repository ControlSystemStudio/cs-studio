/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmFont;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
/** This is a helper class to for XML related test case classes.
 */
public class XMLFileHandler extends TestCase {

	/**
	 *  A dummy method to prevent a warning that this TestCase has no tests.
	 */
	public void testDummy() {
	}
	
	public static Document createDomDocument() throws EdmException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.newDocument();

		} catch (ParserConfigurationException e) {
			throw new EdmException(EdmException.DOM_BUILDER_EXCEPTION, 
					"Error instantiating DOM document.",e);
		}
	}

	public static void writeXML(Document doc) {
		try {

			OutputFormat format = new OutputFormat(doc);
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(doc);

			System.out.println(out.toString());
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
	}

	public static Document readXml(String fileName) {
		try {
			File file = new File(fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(file);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void isColorElementEqual(EdmColor c, String tag, Element e) {
		Element subElement = (Element)e.getElementsByTagName(tag).item(0);
		Element colorElement = (Element)subElement.getElementsByTagName("color").item(0);

		if (colorElement.hasAttribute("name")) {
			assertEquals(c.getName(), colorElement.getAttribute("name"));
		}
		else {
			assertEquals(String.valueOf(c.getRed() / 256), colorElement.getAttribute("red"));
			assertEquals(String.valueOf(c.getGreen() / 256), colorElement.getAttribute("green"));
			assertEquals(String.valueOf(c.getBlue() / 256), colorElement.getAttribute("blue"));

			if (colorElement.hasAttribute("blinkRed"))
				assertEquals(String.valueOf(c.getBlinkRed() / 256), colorElement.getAttribute("blinkRed"));
			if (colorElement.hasAttribute("blinkGreen"))
				assertEquals(String.valueOf(c.getBlinkGreen() / 256), colorElement.getAttribute("blinkGreen"));
			if (colorElement.hasAttribute("blinkBlue"))
				assertEquals(String.valueOf(c.getBlinkBlue() / 256), colorElement.getAttribute("blinkBlue"));
		}
	}

	public static void isColorElementEqual(
			String name,
			int red, int green, int blue,
			int blinkRed, int blinkBlue, int blinkGreen,
			String tag, Element e) {

		Element subElement = (Element)e.getElementsByTagName(tag).item(0);
		Element colorElement = (Element)subElement.getElementsByTagName("color").item(0);

		if (colorElement.hasAttribute("name")) {
			assertEquals(name, colorElement.getAttribute("name"));
		} else {
			assertEquals(String.valueOf(red), colorElement.getAttribute("red"));
			assertEquals(String.valueOf(green), colorElement.getAttribute("green"));
			assertEquals(String.valueOf(blue), colorElement.getAttribute("blue"));

			if (colorElement.hasAttribute("blinkRed"))
				assertEquals(String.valueOf(blinkRed), colorElement.getAttribute("blinkRed"));
			if (colorElement.hasAttribute("blinkGreen"))
				assertEquals(String.valueOf(blinkGreen), colorElement.getAttribute("blinkGreen"));
			if (colorElement.hasAttribute("blinkBlue"))
				assertEquals(String.valueOf(blinkBlue), colorElement.getAttribute("blinkBlue"));
		}
	}

	public static void isElementEqual(String expectedValue, String tag, Element e) {
		Element subElement = (Element)e.getElementsByTagName(tag).item(0);
		assertEquals(expectedValue, subElement.getTextContent());
	}

	public static void isFontElementEqual(String expectedValue, String tag,	Element e) throws EdmException {
		EdmFont f = new EdmFont(new EdmAttribute(expectedValue), true);

		Element subElement = (Element)e.getElementsByTagName(tag).item(0);
		Element fontElement = (Element)subElement.getElementsByTagName("font").item(0);

		assertTrue(fontElement.hasAttribute("fontName"));
		assertEquals(f.getName(), fontElement.getAttribute("fontName"));
		assertTrue(fontElement.hasAttribute("height"));
		assertEquals(String.valueOf(f.getSize()), fontElement.getAttribute("height"));
		int s = 0;
		if (f.isItalic())
			s = s + 2;
		if (f.isBold())
			s = s + 1;
		String style = String.valueOf(s);
		assertEquals(style, fontElement.getAttribute("style"));
	}
	
	/** Returns true if parent element has a child element with the given name.
	 *  Descendants of children are ignored.
	 */
	public static boolean isChildElement(String childName, Element parent) {
		Node node = parent.getFirstChild();
		while (node != null) {
			if (childName.equals(((Element)node).getLocalName())) {
				return true;
			}
			node = node.getNextSibling();
		}
		return false;		
	}
}
