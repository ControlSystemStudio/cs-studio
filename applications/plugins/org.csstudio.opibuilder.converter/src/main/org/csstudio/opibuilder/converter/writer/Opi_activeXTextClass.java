package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeXTextClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeXTextClass.
 * @author Matevz
 *
 */
public class Opi_activeXTextClass extends OpiWidget {
	
	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeXTextClass");
	private static final String typeId = "Label";	
	private static final String name = "EDM Label";
	private static final String version = "1.0";
	
	public Opi_activeXTextClass(Document doc, Element parent, Edm_activeXTextClass t) {
		super(doc, parent, typeId);
		
		element.setAttribute("version", version);
		
		new OpiString(doc, element, "name", name);
		new OpiInt(doc, element, "x", t.getX());
		new OpiInt(doc, element, "y", t.getY());
		new OpiInt(doc, element, "width", t.getW());
		new OpiInt(doc, element, "height", t.getH());
		
		new OpiFont(doc, element, "font", t.getFont());
		new OpiColor(doc, element, "color_foreground", t.getFgColor());
		new OpiColor(doc, element, "color_background", t.getBgColor());
		
		new OpiString(doc, element, "text", t.getValue());
		new OpiBoolean(doc, element, "auto_size", t.isAutoSize());
		
		// There is no border (border style == 0) when border attribute is not set. 
		int borderStyle = 0;
		if (t.isBorder()) {
			// From EDM C code it looks like activeXText always uses solid style. 
			borderStyle = 1;
		}
		new OpiInt(doc, element, "border_style", borderStyle);
		
		if (t.getLineWidth().isInitialized()) {
			new OpiInt(doc, element, "border_width", t.getLineWidth().get());
		}
		
		// It is not clear where the border color and width should be set from.
		//new OpiColor(doc, element, "border_color", ?);
		//new OpiColor(doc, element, "border_width", ?);
		
		// Transparency is true only when useDisplayBg attribute is present.
		new OpiBoolean(doc, element, "transparency", t.isUseDisplayBg());
		
		log.debug("Edm_activeXTextClass written.");
	}

}
