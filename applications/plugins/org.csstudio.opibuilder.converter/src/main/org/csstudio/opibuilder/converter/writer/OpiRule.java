/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XML output class for OPI Rule type.
 * @author Xihui Chen
 */
public class OpiRule{

	/**
	 * Creates an OPI Rule property.
	 * @param widgetContext
	 * @param ruleName
	 * @param prop_id opi property id
	 * @param output_exp true if output expression
	 * @param pvNames PV inputs of the rule
	 * @param expressions The expressions list. Key is the boolean expression, Value is 
	 * the value xml element.
	 */
	public OpiRule(Context widgetContext, String ruleName, String prop_id, boolean output_exp,
			List<String> pvNames, LinkedHashMap<String, Element> expressions) {
		if(widgetContext.getElement().getElementsByTagName("rules").getLength()<=0){			
			widgetContext.getElement().appendChild(widgetContext.getDocument().createElement("rules"));
		}
		Node rulesNode = widgetContext.getElement().getElementsByTagName("rules").item(0);
		Element ruleNode = widgetContext.getDocument().createElement("rule");
		rulesNode.appendChild(ruleNode);
		
		ruleNode.setAttribute("name", ruleName);
		ruleNode.setAttribute("prop_id", prop_id);
		ruleNode.setAttribute("out_exp", String.valueOf(output_exp));
		for (Entry<String, Element> entry : expressions.entrySet()) {
			Element expNode = widgetContext.getDocument().createElement("exp");
			expNode.setAttribute("bool_exp", entry.getKey());
			ruleNode.appendChild(expNode);			
			expNode.appendChild(entry.getValue());
		}
		for(String pv : pvNames){
			Element pvNode = widgetContext.getDocument().createElement("pv");
			pvNode.setAttribute("trig", "true");
			pvNode.setTextContent(pv);
			ruleNode.appendChild(pvNode);
		}	
	}
}
