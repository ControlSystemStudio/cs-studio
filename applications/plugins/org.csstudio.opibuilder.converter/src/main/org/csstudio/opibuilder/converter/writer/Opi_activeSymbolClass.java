/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.converter.model.EdmDouble;
import org.csstudio.opibuilder.converter.model.EdmInt;
import org.csstudio.opibuilder.converter.model.Edm_activeSymbolClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeSymbolClass
 * 
 * @author Xihui Chen
 */
public class Opi_activeSymbolClass extends OpiWidget {

	private static final String typeId = "linkingContainer";
	private static final String name = "EDM Symbol";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeSymbolClass(Context con, Edm_activeSymbolClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);

		if (r.getFile() != null) {
			new OpiString(widgetContext, "opi_file", convertFileExtention(r.getFile()));
		}
		new OpiInt(widgetContext, "border_style", 0);
		new OpiString(widgetContext, "group_name", "1");
		//single pv, no truth table
		if (!r.isTruthTable() && r.getNumPvs() == 1 && r.getControlPvs()!=null) {
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();

			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0;
				double max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				expressions.put("pv0>=" + min + "&&pv0<" + max, valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);

			new OpiRule(widgetContext, "symbol_single_pv", "group_name", false, Arrays.asList(convertPVName(r
					.getControlPvs().getEdmAttributesMap().get("0").get())), expressions);
		}else if(r.isTruthTable() && r.getNumPvs() >0 && r.getControlPvs()!=null){ //binary truth table
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();
			StringBuilder pvsb = new StringBuilder("(");
			for(int i=0; i<r.getNumPvs(); i++){
				if(i!=0)
					pvsb.append("+");
				pvsb.append("(pv").append(i).append("==0?0:1)*").append(Math.pow(2, i));				
			}
			pvsb.append(")");
			String pvs = pvsb.toString();
			
			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0, max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				StringBuilder sb = new StringBuilder();
				sb.append(pvs).append(">=").append(min).append("&&").append(pvs).append("<").append(max);				
				expressions.put(sb.toString(), valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);
			
			List<String> pvnames = new ArrayList<String>();
			for(int i=0; i<r.getNumPvs(); i++){
				pvnames.add(convertPVName(
						r.getControlPvs().getEdmAttributesMap().get(""+i).get()));
			}
			new OpiRule(widgetContext, "symbol_binary_truth_table", "group_name", false, pvnames, expressions);
		}else if(!r.isTruthTable() && r.getNumPvs()>1 && r.getControlPvs()!=null){ //multiple bit fields, no binary truth table
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> andMap = r.getAndMask().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> xorMap = r.getXorMask().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> shiftMap = r.getShiftCount().getEdmAttributesMap();

			StringBuilder pvsb = new StringBuilder("(");
			for(int i=0; i<r.getNumPvs(); i++){
				if(i!=0)
					pvsb.append("|");
				int andMask=0, xorMask =0, shiftCount=0;
				String key = ""+i;
				if(andMap.containsKey(key)){
					andMask = andMap.get(key).get();
				}
				if(xorMap.containsKey(key)){
					xorMask = xorMap.get(key).get();
				}
				if(shiftMap.containsKey(key)){
					shiftCount = shiftMap.get(key).get();
				}				
				pvsb.append("(pvInt").append(i).append('&').append(andMask).append('^').append(xorMask).append("<<").append(shiftCount).append(')');				
			}
			pvsb.append(")");
			String pvs = pvsb.toString();
			
			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0, max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				StringBuilder sb = new StringBuilder();
				sb.append(pvs).append(">=").append(min).append("&&").append(pvs).append("<").append(max);				
				expressions.put(sb.toString(), valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);
			
			List<String> pvnames = new ArrayList<String>();
			for(int i=0; i<r.getNumPvs(); i++){
				pvnames.add(convertPVName(
						r.getControlPvs().getEdmAttributesMap().get(""+i).get()));
			}
			new OpiRule(widgetContext, "symbol_multi_pvs", "group_name", false, pvnames, expressions);
		
		}

	}

}
