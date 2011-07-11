/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.core.runtime.Path;
import org.jdom.Element;

/**The property for script.
 * @author Xihui Chen
 *
 */
public class ScriptProperty extends AbstractWidgetProperty {
	
	/**
	 * XML ELEMENT name <code>PATH</code>.
	 */
	public static final String XML_ELEMENT_PATH = "path"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>PATHSTRING</code>.
	 */
	public static final String XML_ATTRIBUTE_PATHSTRING = "pathString"; //$NON-NLS-1$
	
	public static final String XML_ATTRIBUTE_CHECKCONNECT = "checkConnect"; //$NON-NLS-1$
	
	public static final String XML_ATTRIBUTE_SKIP_FIRST_EXECUTION = "sfe"; //$NON-NLS-1$
	public static final String XML_ATTRIBUTE_STOP_EXECUTE_ON_ERROR = "seoe"; //$NON-NLS-1$
	
	/**
	 * XML Element name <code>PV</code>.
	 */
	public static final String XML_ELEMENT_PV = "pv"; //$NON-NLS-1$

	public static final String XML_ATTRIBUTE_TRIGGER = "trig"; //$NON-NLS-1$

	/**Script Property Constructor. The property value type is {@link ScriptsInput}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 */
	public ScriptProperty(String prop_id, String description,
			WidgetPropertyCategory category) {
		super(prop_id, description, category, new ScriptsInput());
		
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		ScriptsInput acceptableValue = null;
		if(value instanceof ScriptsInput){
			acceptableValue = (ScriptsInput)value;			
		}
		
		return acceptableValue;
	}

	
	@Override
	public Object getPropertyValue() {
		if(executionMode == ExecutionMode.RUN_MODE && widgetModel !=null){
			ScriptsInput value = (ScriptsInput) super.getPropertyValue();
			for(ScriptData sd : value.getScriptList()){
				for(Object pv : sd.getPVList().toArray()){
					PVTuple pvTuple = (PVTuple)pv;
					String newPV = OPIBuilderMacroUtil.replaceMacros(widgetModel, pvTuple.pvName);
					if(!newPV.equals(pvTuple.pvName)){
						int i= sd.getPVList().indexOf(pv);
						sd.getPVList().remove(pv);
						sd.getPVList().add(i, new PVTuple(newPV, pvTuple.trigger));
					}
				}
			}
			return value;
		}else
			return super.getPropertyValue();
	}
	
	


	@Override
	public ScriptsInput readValueFromXML(Element propElement) {
		ScriptsInput result = new ScriptsInput();
		for(Object oe : propElement.getChildren(XML_ELEMENT_PATH)){
			Element se = (Element)oe;
			ScriptData  sd = new ScriptData(new Path(se.getAttributeValue(XML_ATTRIBUTE_PATHSTRING)));
			if(se.getAttributeValue(XML_ATTRIBUTE_CHECKCONNECT) != null)
				sd.setCheckConnectivity(
						Boolean.parseBoolean(se.getAttributeValue(XML_ATTRIBUTE_CHECKCONNECT)));
			if(se.getAttributeValue(XML_ATTRIBUTE_SKIP_FIRST_EXECUTION) != null)
				sd.setSkipPVsFirstConnection(
						Boolean.parseBoolean(se.getAttributeValue(XML_ATTRIBUTE_SKIP_FIRST_EXECUTION)));
			if(se.getAttributeValue(XML_ATTRIBUTE_STOP_EXECUTE_ON_ERROR) != null)
				sd.setStopExecuteOnError(
						Boolean.parseBoolean(se.getAttributeValue(XML_ATTRIBUTE_STOP_EXECUTE_ON_ERROR)));
			for(Object o : se.getChildren(XML_ELEMENT_PV)){
				Element pve = (Element)o;
				boolean trig = true;
				if(pve.getAttribute(XML_ATTRIBUTE_TRIGGER) != null)
					trig = Boolean.parseBoolean(pve.getAttributeValue(XML_ATTRIBUTE_TRIGGER));
				sd.addPV(new PVTuple(pve.getText(), trig));
			}
			result.getScriptList().add(sd);			
		}		
		return result;
	}

	@Override
	public void writeToXML(Element propElement) {
		for(ScriptData scriptData : ((ScriptsInput)getPropertyValue()).getScriptList()){
				Element pathElement = new Element(XML_ELEMENT_PATH);
				pathElement.setAttribute(XML_ATTRIBUTE_PATHSTRING, 
						scriptData.getPath().toPortableString());
				pathElement.setAttribute(XML_ATTRIBUTE_CHECKCONNECT,
						Boolean.toString(scriptData.isCheckConnectivity()));
				pathElement.setAttribute(XML_ATTRIBUTE_SKIP_FIRST_EXECUTION,
						Boolean.toString(scriptData.isSkipPVsFirstConnection()));
				pathElement.setAttribute(XML_ATTRIBUTE_STOP_EXECUTE_ON_ERROR,
						Boolean.toString(scriptData.isStopExecuteOnError()));
				for(PVTuple pv : scriptData.getPVList()){
					Element pvElement = new Element(XML_ELEMENT_PV);
					pvElement.setText(pv.pvName);
					pvElement.setAttribute(XML_ATTRIBUTE_TRIGGER, Boolean.toString(pv.trigger));
					pathElement.addContent(pvElement);
				}
				propElement.addContent(pathElement);
		}		
	}

}
