/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.LinkedHashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XML output class for OPI Script property.
 * @author Xihui Chen
 */
public class OpiScript{
	
	/**Creates an OPI Script property with checkConnect=true, skipFirstTrigger=false, stopExecutionOnError=false.
	 * @param widgetContext
	 * @param scriptName
	 * @param pvNames trigger pv names
	 * @param scriptText script text
	 */
	public OpiScript(Context widgetContext, String scriptName, 
			LinkedHashMap<String, Boolean> pvNames, String scriptText){
		this(widgetContext, scriptName, pvNames, scriptText, true, false, false);
	}

	/**Creates an OPI Script property.
	 * @param widgetContext
	 * @param scriptName
	 * @param pvNames trigger pv names
	 * @param scriptText script text
	 * @param checkConnected check if all pvs are connected before execution. In most cases, it is true
	 * @param skipFirstTrigger skip the first triggered execution because of PV initialization, It is false in most cases.
	 * @param stopExecutionOnError stop following executions on error. It is false in most cases.
	 */
	public OpiScript(Context widgetContext, String scriptName, 
			LinkedHashMap<String, Boolean> pvNames, String scriptText, boolean checkConnected, boolean skipFirstTrigger, boolean stopExecutionOnError) {
		if(widgetContext.getElement().getElementsByTagName("scripts").getLength()<=0){			
			widgetContext.getElement().appendChild(widgetContext.getDocument().createElement("scripts"));
		}
		Node scriptsNode = widgetContext.getElement().getElementsByTagName("scripts").item(0);
		Element scriptNode = widgetContext.getDocument().createElement("path");	
		
		scriptsNode.appendChild(scriptNode);
		
		scriptNode.setAttribute("pathString", "EmbeddedJs");
		scriptNode.setAttribute("checkConnect", String.valueOf(checkConnected));
		scriptNode.setAttribute("sfe", String.valueOf(skipFirstTrigger));
		scriptNode.setAttribute("seoe", String.valueOf(stopExecutionOnError));
		
		Element scriptNameNode=  widgetContext.getDocument().createElement("scriptName");	
		scriptNameNode.setTextContent(scriptName);
		scriptNode.appendChild(scriptNameNode);		
		
		Element scriptTextNode=  widgetContext.getDocument().createElement("scriptText");	
		scriptTextNode.setTextContent(scriptText);
		scriptNode.appendChild(scriptTextNode);		
	
		for(String pv : pvNames.keySet()){
			Element pvNode = widgetContext.getDocument().createElement("pv");
			pvNode.setAttribute("trig", pvNames.get(pv).toString());
			pvNode.setTextContent(pv);
			scriptNode.appendChild(pvNode);
		}	
	}
}
