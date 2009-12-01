package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * General OPI output class for EdmAttribute.
 * Creates an empty element: <name></name>
 * @author Matevz
 *
 */
public class OpiAttribute {

	protected Element element;
	
	public OpiAttribute(Document doc, Element parent, String name) {
		
		this.element = doc.createElement(name);
	    parent.appendChild(element);
	    
	}

}
