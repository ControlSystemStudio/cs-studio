package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmDisplay data.
 * @author Matevz
 *
 */
public class OpiDisplay {

	private static final String version = "1.0";

	public OpiDisplay(Document doc, EdmDisplay d, String displayFileName) {

		Element element = doc.createElement("display");
		element.setAttribute("typeId", "org.csstudio.opibuilder.Display");
		doc.appendChild(element);
		
		writeHeader(doc, element, d);
		OpiWriter.writeWidgets(doc, element, d.getWidgets());
	}

	private void writeHeader(Document doc, Element element, EdmDisplay d) {

		element.setAttribute("version", version);
		
		new OpiInt(doc, element, "x", d.getX());
		new OpiInt(doc, element, "y", d.getY());
		new OpiInt(doc, element, "width", d.getW());
		new OpiInt(doc, element, "height", d.getH());
		
		new OpiFont(doc, element, "font", d.getFont());
		new OpiFont(doc, element, "font_ctl", d.getCtlFont());
		new OpiFont(doc, element, "font_button", d.getBtnFont());
		
		new OpiColor(doc, element, "color_foreground", d.getFgColor());
		new OpiColor(doc, element, "color_background", d.getBgColor());
		new OpiColor(doc, element, "color_text", d.getTextColor());
		new OpiColor(doc, element, "color_ctlFgColor1", d.getCtlFgColor1());
		new OpiColor(doc, element, "color_ctlFgColor2", d.getCtlFgColor2());
		new OpiColor(doc, element, "color_ctlBgColor1", d.getCtlBgColor1());
		new OpiColor(doc, element, "color_ctlBgColor2", d.getCtlBgColor2());
		new OpiColor(doc, element, "color_topshadowcolor", d.getTopShadowColor());
		new OpiColor(doc, element, "color_botshadowcolor", d.getBotShadowColor());
		
		if (d.getAttribute("title").isInitialized())
			new OpiString(doc, element, "name", d.getTitle());
		new OpiBoolean(doc, element, "grid_show", d.isShowGrid());
		if (d.getAttribute("gridSize").isInitialized())
			new OpiInt(doc, element, "grid_space", d.getGridSize());
		new OpiBoolean(doc, element, "scroll_disable", d.isDisableScroll());
		
	}

}