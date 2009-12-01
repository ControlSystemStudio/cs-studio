package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeGroupClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeGroupClass
 * @author Matevz
 *
 */
public class Opi_activeGroupClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeGroupClass");
	private static final String typeId = "groupingContainer";
	private static final String version = "1.0";
	
	public Opi_activeGroupClass(Document doc, Element parent, Edm_activeGroupClass g) {
		super(doc, parent, typeId);
		
		element.setAttribute("version", version);
		
		new OpiInt(doc, element, "x", g.getX());
		new OpiInt(doc, element, "y", g.getY());
		new OpiInt(doc, element, "width", g.getW());
		new OpiInt(doc, element, "height", g.getH());

		OpiWriter.writeWidgets(doc, element, g.getWidgets());
		
		log.debug("Edm_activeGroupClass written.");
	}
}