/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.converter.model.EdmString;
import org.csstudio.opibuilder.converter.model.Edm_activePipClass;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;

/**
 * XML conversion class for Edm_activePipClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activePipClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activePipClass");
	private static final String typeId = "linkingContainer";
	private static final String name = "EDM linkingContainer";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activePipClass(Context con, Edm_activePipClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);
		

		if(r.getDisplaySource()!=null)
		{
			if(r.getDisplaySource().equals("menu") && r.getDisplayFileName()!=null && r.getFilePv()!=null){
				
				Map<String, EdmString> displayFileMap = r.getDisplayFileName().getEdmAttributesMap();
				if(displayFileMap.size()>0){
					
					Map<String, EdmString> symbolsMap=null;
					if(r.getSymbols()!=null)
						symbolsMap = r.getSymbols().getEdmAttributesMap();

					StringBuilder sb = new StringBuilder("importPackage(Packages.org.csstudio.opibuilder.scriptUtil);\n");
					sb.append("var pv0 = PVUtil.getLong(pvs[0]);\n");
					boolean f= false;
					for(String key : displayFileMap.keySet()){
						if(f)
							sb.append("else ");						
						f=true;
						sb.append("if(pv0=="+key+"){\n");
						boolean append = true;
						if(r.getReplaceSymbols()!=null&&r.getReplaceSymbols().getEdmAttributesMap().containsKey(key)){
							append = !r.getReplaceSymbols().getEdmAttributesMap().get(key).is();
						}
						sb.append("var macroInput = DataUtil.createMacrosInput("+append+");\n");
						if(symbolsMap != null && symbolsMap.containsKey(key)){
							try {
								EdmString symbols = symbolsMap.get(key);
								if (symbols != null) {
									for (String s : StringSplitter.splitIgnoreInQuotes(symbols.get(), ',', true)) {
										String[] rs = StringSplitter.splitIgnoreInQuotes(s, '=', true);
										if (rs.length == 2) {
											try {
												sb.append("macroInput.put(\"" + rs[0] + "\", \"" + rs[1]+"\");\n");
											} catch (Exception e) {
												ErrorHandlerUtil.handleError("Parse Macros Error on: "+s, e);
											}
										}
									}
								}
							} catch (Exception e) {
								ErrorHandlerUtil.handleError("Parse Macros Error", e);
							}
						}
						sb.append("widget.setPropertyValue(\"macros\", macroInput);\n");
						
						sb.append("widget.setPropertyValue(\"opi_file\",\""+
								convertFileExtention(displayFileMap.get(key).get()) +"\");\n");
						
						sb.append("}\n");
					}
					
					LinkedHashMap<String, Boolean> pvNames = new LinkedHashMap<String, Boolean>();
					pvNames.put(convertPVName(r.getFilePv()), true);
					
					new OpiScript(widgetContext, "OPIFileScript", pvNames , sb.toString());
				}
				
				
				
				
			}else if (r.getFile() != null) {
				String originPath = r.getFile();
				originPath = convertFileExtention(originPath);
				new OpiString(widgetContext, "opi_file", originPath);
			}
		}
		
		if(r.getDisplaySource()==null && r.getFilePv()!=null){
			createPVOutputRule(r, convertPVName(r.getFilePv()), "opi_file", "pvStr0", "OPIFileFromPVRule");
		}
		
		log.debug("Edm_activePipClass written.");

	}



}

