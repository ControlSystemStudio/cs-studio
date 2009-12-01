package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmString type.
 * Creates an element: <name>stringValue</name>
 * @author Matevz
 *
 */
public class OpiString extends OpiAttribute {

	public OpiString(Document doc, Element parent, String name, EdmString s) {
		this(doc, parent, name, s.get());
	}
	
	public OpiString(Document doc, Element parent, String name, String s) {
		super(doc, parent, name);
		element.appendChild(doc.createTextNode(String.valueOf(s)));
	}
}
