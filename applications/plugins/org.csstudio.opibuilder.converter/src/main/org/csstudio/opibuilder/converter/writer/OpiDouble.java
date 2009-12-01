package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDouble;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmDouble type.
 * Creates an element: <name>doubleValue</name>
 * @author Matevz
 *
 */
public class OpiDouble extends OpiAttribute {

	public OpiDouble(Document doc, Element parent, String name, EdmDouble d) {
		super(doc, parent, name);
		element.appendChild(doc.createTextNode(String.valueOf(d.get())));
	}
	
}
