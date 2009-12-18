package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_TextupdateClass;

/**
 * XML conversion class for Edm_TextupdateClass.
 * @author Matevz
 */
public class Opi_TextupdateClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_TextupdateClass");
	private static final String typeId = "TextUpdate";	
	private static final String name = "EDM Text Update";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_TextupdateClass to OPI TextUpdate widget XML.  
	 */
	public Opi_TextupdateClass(Context con, Edm_TextupdateClass t) {
		super(con);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);

		new OpiString(context, "name", name);
		new OpiInt(context, "x", t.getX() - context.getX());
		new OpiInt(context, "y", t.getY() - context.getY());
		new OpiInt(context, "width", t.getW());
		new OpiInt(context, "height", t.getH());

		new OpiString(context, "pv_name", t.getControlPv());
		
		new OpiColor(context, "color_foreground", t.getFgColor());
		new OpiColor(context, "color_background", t.getBgColor());
		new OpiBoolean(context, "color_fill", t.isFill());
		
		new OpiFont(context, "font", t.getFont());
		if (t.getAttribute("fontAlign").isInitialized())
			new OpiString(context, "font_align", t.getFontAlign());
		
		if (t.getAttribute("lineWidth").isInitialized()) { 
			new OpiInt(context, "border_width", t.getLineWidth());
		}
		boolean lineAlarm = t.getAttribute("lineAlarm").isInitialized() && t.isLineAlarm();
		new OpiBoolean(context, "border_alarmsensitive", lineAlarm);
		
		boolean fgAlarm = t.getAttribute("fgAlarm").isInitialized() && t.isFgAlarm();
		new OpiBoolean(context, "foregroundcolor_alarmsensitive", fgAlarm);

		log.debug("Edm_TextupdateClass written.");
	}
}
