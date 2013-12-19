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

import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.csstudio.opibuilder.converter.model.Edm_activeMessageButtonClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeRectangleClass
 * 
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeMessageButtonClass extends OpiWidget {

	private static final String typeId = "BoolButton";
	private static final String name = "EDM Message Button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeMessageButtonClass(Context con, Edm_activeMessageButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);

		if (r.getControlPv() != null) {
			createOnOffColorRule(r, convertPVName(r.getControlPv()), "background_color", r.getOnColor(),
					r.getOffColor(), "OnOffBackgroundRule");
			Element pvNameNode;
			Element valueNode;
			if(r.getPressValue()!=null){
				pvNameNode = widgetContext.getDocument().createElement("pv_name");
				pvNameNode.setTextContent(convertPVName(r.getControlPv()));
				valueNode = widgetContext.getDocument().createElement("value");
				valueNode.setTextContent(r.getPressValue());
				new OpiAction(widgetContext, "WRITE_PV", Arrays.asList(pvNameNode, valueNode),
						false, false);
			}
			if (r.getReleaseValue() != null) {
				pvNameNode = widgetContext.getDocument().createElement("pv_name");
				pvNameNode.setTextContent(convertPVName(r.getControlPv()));
				valueNode = widgetContext.getDocument().createElement("value");
				valueNode.setTextContent(r.getReleaseValue());
				new OpiAction(widgetContext, "WRITE_PV", Arrays.asList(pvNameNode, valueNode),
						false, false);
			}
			new OpiInt(widgetContext, "push_action_index", r.getPressValue()==null?1:0);
			new OpiInt(widgetContext, "released_action_index", r.getPressValue()==null?0:1);

		}
		new OpiColor(widgetContext, "on_color", r.getOnColor(), r);
		new OpiColor(widgetContext, "off_color", r.getOffColor(), r);

		if (r.getOnLabel() != null)
			new OpiString(widgetContext, "on_label", r.getOnLabel());
		if (r.getOffLabel() != null)
			new OpiString(widgetContext, "off_label", r.getOffLabel());

		new OpiBoolean(widgetContext, "toggle_button", r.isToggle());
		if (r.getPassword() != null)
			new OpiString(widgetContext, "password", r.getPassword());
		new OpiInt(widgetContext, "show_confirm_dialog", r.getPassword() != null ? 1 : 0);

		new OpiBoolean(widgetContext, "show_led", false);
		new OpiBoolean(widgetContext, "show_boolean_label", true);
		new OpiBoolean(widgetContext, "square_button", true);

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
		colorNode.setAttribute("name", onColor.getName());
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(onColor.getRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(onColor.getGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(onColor.getBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("widget.getValue().booleanValue()", valueNode);

		valueNode = widgetContext.getDocument().createElement("value");
		colorNode = widgetContext.getDocument().createElement("color");
		colorNode.setAttribute("name", offColor.getName());
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(offColor.getRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(offColor.getGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(offColor.getBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("true", valueNode);

		new OpiRule(widgetContext, ruleName, opiProperty, false, Arrays.asList(pvName), expressions);
	}

}
