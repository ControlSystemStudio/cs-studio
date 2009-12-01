package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmInt type.
 * Creates an element: <name>intValue</name>
 * @author Matevz
 *
 */
public class OpiInt extends OpiAttribute {

	public OpiInt(Document doc, Element parent, String name, EdmInt i) {
		this(doc, parent, name, i.get());
	}
	
	public OpiInt(Document doc, Element parent, String name, int i) {
		super(doc, parent, name);
		element.appendChild(doc.createTextNode(String.valueOf(i)));
	}
}
