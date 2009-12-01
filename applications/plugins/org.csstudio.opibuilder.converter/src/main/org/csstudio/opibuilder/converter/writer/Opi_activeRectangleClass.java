package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeRectangleClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 *
 */
public class Opi_activeRectangleClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRectangleClass");
	private static final String typeId = "Rectangle";
	private static final String name = "EDM Rectangle";
	private static final String version = "1.0";
	
	public Opi_activeRectangleClass(Document doc, Element parent, Edm_activeRectangleClass r) {
		super(doc, parent, typeId);
		
		element.setAttribute("version", version);
		
		/* It is not clear how rectangle attributes are mapped precisely since the
		 * mapping is not one to one.
		 * 
		 * For now, the mapping is as follows:
		 * 
		 * line* Edm attributes are converted into border* Opi attributes.
		 * fill* Edm attributes are converted into background* Opi attributes.
		 * 
		 * It is not yet defined what to do with visibility* Edm attributes and
		 * line* /foreground* Opi attributes.
		 */

		new OpiString(doc, element, "name", name);

		new OpiInt(doc, element, "x", r.getX());
		new OpiInt(doc, element, "y", r.getY());
		new OpiInt(doc, element, "width", r.getW());
		new OpiInt(doc, element, "height", r.getH());
		
		new OpiColor(doc, element, "border_color", r.getLineColor());
		if (r.getFillColor().isInitialized()) {
			new OpiColor(doc, element, "color_background", r.getFillColor());
		}
		
		if (r.getLineWidth().isInitialized()) {
			new OpiInt(doc, element, "border_width", r.getLineWidth().get());
		}

		/* It is not clear when there is no border for EDM display. 
		 * 
		 * Here it is assumed there is no border (border style == 0) when line style
		 * is not set. 
		 * 
		 * The alternative would be to use lineWidth? 
		 */
		int borderStyle = 0;
		if (r.getLineStyle().isInitialized()) {
			
			switch (r.getLineStyle().get()) {
			case EdmLineStyle.SOLID: {
				borderStyle = 1;
			} break;
			case EdmLineStyle.DASH: {
				borderStyle = 9;
			} break;
			}
			
		}
		new OpiInt(doc, element, "border_style", borderStyle);
		
		log.debug("Edm_activeRectangleClass written.");
		
	}

}