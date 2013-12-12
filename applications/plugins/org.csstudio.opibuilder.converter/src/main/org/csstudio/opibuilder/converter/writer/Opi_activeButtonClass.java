/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.csstudio.opibuilder.converter.model.Edm_activeButtonClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeRectangleClass
 * 
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeButtonClass extends OpiWidget {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeButtonClass");
	private static final String typeId = "BoolButton";
	private static final String name = "EDM Button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeButtonClass(Context con, Edm_activeButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);

		if (r.getControlPv() != null) {
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
			createOnOffColorRule(r, convertPVName(r.getControlPv()), "background_color", r.getOnColor(),
					r.getOffColor(), "OnOffBackgroundRule");
		}
		new OpiColor(widgetContext, "on_color", r.getOnColor(), r);
		new OpiColor(widgetContext, "off_color", r.getOffColor(), r);

		if (r.getOnLabel() != null)
			new OpiString(widgetContext, "on_label", r.getOnLabel());
		if (r.getOffLabel() != null)
			new OpiString(widgetContext, "off_label", r.getOffLabel());
		new OpiBoolean(widgetContext, "show_boolean_label", true);

		new OpiBoolean(widgetContext, "toggle_button", r.getButtonType() != null
				&& r.getButtonType().equals("push"));
		if (r.getAttribute("controlBitsPos").isExistInEDL()) {
			new OpiInt(widgetContext, "data_type", r.getControlBitsPos() < 0 ? 0
					: r.getControlBitsPos());
		}else{
			new OpiInt(widgetContext, "data_type", 1);
			new OpiString(widgetContext, "on_state", "1");
			new OpiString(widgetContext, "off_state", "0");			
		}
		
		new OpiBoolean(widgetContext, "show_led", false);
		new OpiBoolean(widgetContext, "square_button", true);

		log.debug("Edm_activeButtonClass written.");

	}

	/**
	 * Create a rule that make a color property alarm sensitive.
	 * 
	 * @param edmWidgetClass
	 * @param edmAlarmAttr
	 * @param edmAlarmPVAttr
	 * @param opiProperty
	 */
	protected void createOnOffColorRule(EdmWidget edmWidgetClass, String pvName,
			String opiProperty, EdmColor onColor, EdmColor offColor, String ruleName) {
		LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
		Element valueNode;
		Element colorNode;
		valueNode = widgetContext.getDocument().createElement("value");
		colorNode = widgetContext.getDocument().createElement("color");
		colorNode.setAttribute("name", offColor.getName());
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(offColor.getRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(offColor.getGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(offColor.getBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("pv0==0", valueNode);

		valueNode = widgetContext.getDocument().createElement("value");
		colorNode = widgetContext.getDocument().createElement("color");
		colorNode.setAttribute("name", onColor.getName());
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(onColor.getRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(onColor.getGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(onColor.getBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("true", valueNode);

		new OpiRule(widgetContext, ruleName, opiProperty, false, Arrays.asList(pvName), expressions);
	}

}
