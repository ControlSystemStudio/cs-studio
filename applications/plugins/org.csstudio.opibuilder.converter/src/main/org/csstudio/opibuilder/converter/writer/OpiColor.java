/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.w3c.dom.Element;

/**
 * XML output class for EdmColor type.
 * 
 * @author Matevz
 */
public class OpiColor extends OpiAttribute {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.OpiColor");
	
	/**
	 * If EdmColor name is defined, it creates an element <tag> <color
	 * name="colorName" /> </tag>
	 * 
	 * otherwise creates an element
	 * 
	 * <tag> <color blue="blueValue" green="greenValue" red="redValue" /> </tag>
	 */
	public OpiColor(Context widgetContext, String tag, EdmColor c, EdmWidget w) {
		super(widgetContext, tag);
		if (w != null && c.isDynamic() && w.getAlarmPv() != null)
			createColorAlarmRule(widgetContext, OpiWidget.convertPVName(w.getAlarmPv()), tag, c);
		if(c.isBlinking()){
			createBlinkingColorRule(widgetContext, tag, c);
		}
		
		
		Element colorElement = propertyContext.getDocument().createElement("color");
		propertyContext.getElement().appendChild(colorElement);

		String colorName = c.getName();

		if (colorName != null && colorName.length() > 0) {
			colorElement.setAttribute("name", colorName);
			log.debug("Written color: " + colorName);
		} 
		String red = String.valueOf(colorComponentTo8Bits(c.getRed()));
		String green = String.valueOf(colorComponentTo8Bits(c.getGreen()));
		String blue = String.valueOf(colorComponentTo8Bits(c.getBlue()));

		colorElement.setAttribute("red", red);
		colorElement.setAttribute("green", green);
		colorElement.setAttribute("blue", blue);

		log.debug("Written color property with attributes: " + red + ", " + green + ", " + blue);

	}
	
	/**
	 * Create a rule that make a dynamic color
	 */
	protected void createBlinkingColorRule(Context widgetContext,
			String opiProperty, EdmColor c) {
		LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
		Element valueNode;
		Element colorNode;

		valueNode = widgetContext.getDocument().createElement("value");
		colorNode = widgetContext.getDocument().createElement("color");
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(c.getRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(c.getGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(c.getBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("pv0==0", valueNode);
		
		valueNode = widgetContext.getDocument().createElement("value");
		colorNode = widgetContext.getDocument().createElement("color");
		colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(c.getBlinkRed()));
		colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(c.getBlinkGreen()));
		colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(c.getBlinkBlue()));
		valueNode.appendChild(colorNode);
		expressions.put("pv0==1", valueNode);

		new OpiRule(widgetContext, opiProperty + "_"+c.getName(), opiProperty, false,
				Arrays.asList("sim://ramp(0,1,1,0.5)"), expressions);
	}


	/**
	 * Create a rule that make a dynamic color
	 */
	protected void createColorAlarmRule(Context widgetContext, String alarmPVName,
			String opiProperty, EdmColor edmColor) {
		LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
		Element valueNode;
		Element colorNode;

		for (Entry<String, String> entry : edmColor.getRuleMap().entrySet()) {
			String expression = entry.getKey();
			StringBuilder sb = new StringBuilder(expression);
			if (expression.trim().equals("default")) {
				sb = new StringBuilder("true");
			} else {
				Pattern p = Pattern.compile(">={1}|<={1}|>{1}|<{1}|={1}");
				Matcher m = p.matcher(entry.getKey());

				int i = 0;
				int numOfEqual = 0;
				while (m.find()) {
					String in = "pv0";
					boolean isEqual = m.group().equals("=");
					if(isEqual){
						in=in+"=";						
					}
					sb.insert(m.start() + 3 * i+numOfEqual, in);
					if(isEqual){
						numOfEqual++;					
					}
					i++;
				}
			}
			EdmColor c = EdmModel.getColorsList().getColor(entry.getValue());
			if (c != null) {
				valueNode = widgetContext.getDocument().createElement("value");
				colorNode = widgetContext.getDocument().createElement("color");
				colorNode.setAttribute("name", c.getName());
				colorNode.setAttribute("red", "" + OpiColor.colorComponentTo8Bits(c.getRed()));
				colorNode.setAttribute("green", "" + OpiColor.colorComponentTo8Bits(c.getGreen()));
				colorNode.setAttribute("blue", "" + OpiColor.colorComponentTo8Bits(c.getBlue()));
				valueNode.appendChild(colorNode);
				expressions.put(sb.toString(), valueNode);
			}
		}

		new OpiRule(widgetContext, opiProperty + "_"+edmColor.getName(), opiProperty, false,
				Arrays.asList(alarmPVName), expressions);
	}

	/**
	 * Converts the 16 bit color component value to 8 bit and returns it.
	 */
	public static int colorComponentTo8Bits(int colorComponent) {
		return colorComponent >> 8;
	}
}
