package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeXTextClass;

/**
 * XML conversion class for Edm_activeXTextClass.
 * @author Matevz
 */
public class Opi_activeXTextClass extends OpiWidget {
	
	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeXTextClass");
	private static final String typeId = "Label";	
	private static final String name = "EDM Label";
	private static final String version = "1.0";
	
	/**
	 * Converts the Edm_activeXTextClass to OPI Label widget XML.  
	 */
	public Opi_activeXTextClass(Context con, Edm_activeXTextClass t) {
		super(con);
		setTypeId(typeId);
		
		context.getElement().setAttribute("version", version);
		
		new OpiString(context, "name", name);
		new OpiInt(context, "x", t.getX() - context.getX());
		new OpiInt(context, "y", t.getY() - context.getY());
		new OpiInt(context, "width", t.getW());
		new OpiInt(context, "height", t.getH());
		
		new OpiFont(context, "font", t.getFont());
		new OpiColor(context, "color_foreground", t.getFgColor());
		new OpiColor(context, "color_background", t.getBgColor());
		
		new OpiString(context, "text", t.getValue().get());
		
		boolean autoSize = t.getAttribute("autoSize").isInitialized() && t.isAutoSize();
		new OpiBoolean(context, "auto_size", autoSize);
		
		// There is no border (border style == 0) when border attribute is not set. 
		int borderStyle = 0;
		if (t.getAttribute("border").isInitialized() && t.isBorder()) {
			// From EDM C code it looks like activeXText always uses solid style. 
			borderStyle = 1;
		}
		new OpiInt(context, "border_style", borderStyle);
		
		if (t.getAttribute("lineWidth").isInitialized()) {
			new OpiInt(context, "border_width", t.getLineWidth());
		}
		
		// It is not clear where the border color and width should be set from.
		//new OpiColor(context, "border_color", ?);
		//new OpiColor(context, "border_width", ?);
		
		// Transparency is true only when useDisplayBg attribute is present.
		boolean useDisplayBg = t.getAttribute("useDisplayBg").isInitialized() && t.isUseDisplayBg();  
		new OpiBoolean(context, "transparency", useDisplayBg);
		
		log.debug("Edm_activeXTextClass written.");
	}

}
