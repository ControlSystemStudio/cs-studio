package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmBoolean type.
 * Creates an element: <name>booleanValue</name>
 * @author Matevz
 *
 */
public class OpiBoolean extends OpiAttribute {

	public OpiBoolean(Document doc, Element parent, String name, EdmBoolean b) {
		this(doc, parent, name, b.is());
	}
	
	public OpiBoolean(Document doc, Element parent, String name, boolean b) {
		super(doc, parent, name);
		element.appendChild(doc.createTextNode(String.valueOf(b)));
	}
}
