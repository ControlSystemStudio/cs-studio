package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmColor type.
 * Creates an element: 
 * <tag>
 *   	<color blue="blueValue" green="greenValue" red="redValue" />
 * </tag>
 * @author Matevz
 *
 */
public class OpiColor extends OpiAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.OpiColor");
	
	public OpiColor(Document doc, Element parent, String tag, EdmColor c) {
		super(doc, parent, tag);
		
		Element colorElement = doc.createElement("color");
		element.appendChild(colorElement);
		
		String colorName = c.getName();
		
		if (colorName != "") {

			colorElement.setAttribute("name", colorName);
			
			log.debug("Written color: " + colorName);
		}
		else {
			String red = String.valueOf(colorComponentTo8Bits(c.getRed()));
			String green = String.valueOf(colorComponentTo8Bits(c .getGreen()));
			String blue = String.valueOf(colorComponentTo8Bits(c.getBlue()));

			colorElement.setAttribute("red", red);
			colorElement.setAttribute("green", green);
			colorElement.setAttribute("blue", blue);

			log.debug("Written color property with attributes: " + red + ", " + green + ", " + blue);
		}
	}
	
	public static int colorComponentTo8Bits(int colorComponent) {
		return colorComponent / 0x100;
	}
}
