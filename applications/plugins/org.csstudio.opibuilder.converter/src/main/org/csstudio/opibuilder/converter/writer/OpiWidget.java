package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * General class for outputting widgets.
 * Creates element:
 * 		<widget typeId="org.csstudio.opibuilder.widgets.type">
 * 		</widget>
 * @author Matevz
 *
 */
public class OpiWidget extends OpiAttribute {

	public OpiWidget(Document doc, Element parent, String type) {
		super(doc, parent, "widget");
		element.setAttribute("typeId", "org.csstudio.opibuilder.widgets." + type);
	}
}
