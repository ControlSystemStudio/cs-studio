package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_TextupdateClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_TextupdateClass.
 * @author Matevz
 *
 */
public class Opi_TextupdateClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_TextupdateClass");
	private static final String typeId = "TextUpdate";	
	private static final String name = "EDM Text Update";
	private static final String version = "1.0";

	public Opi_TextupdateClass(Document doc, Element parent, Edm_TextupdateClass t) {
		super(doc, parent, typeId);

		element.setAttribute("version", version);

		new OpiString(doc, element, "name", name);
		new OpiInt(doc, element, "x", t.getX());
		new OpiInt(doc, element, "y", t.getY());
		new OpiInt(doc, element, "width", t.getW());
		new OpiInt(doc, element, "height", t.getH());

		new OpiString(doc, element, "pv_name", t.getControlPv());
		
		new OpiColor(doc, element, "color_foreground", t.getFgColor());
		new OpiColor(doc, element, "color_background", t.getBgColor());
		new OpiBoolean(doc, element, "color_fill", t.isFill());
		
		new OpiFont(doc, element, "font", t.getFont());
		if (t.getAttribute("fontAlign").isInitialized())
			new OpiString(doc, element, "font_align", t.getFontAlign());
		
		if (t.getAttribute("lineWidth").isInitialized()) { 
			new OpiInt(doc, element, "border_width", t.getLineWidth());
		}
		new OpiBoolean(doc, element, "border_alarmsensitive", t.isLineAlarm());
		new OpiBoolean(doc, element, "foregroundcolor_alarmsensitive", t.isFgAlarm());

		log.debug("Edm_TextupdateClass written.");
	}
}
