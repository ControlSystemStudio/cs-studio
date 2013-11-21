/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;

import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.csstudio.opibuilder.converter.model.EdmString;
import org.csstudio.opibuilder.converter.model.Edm_relatedDisplayClass;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeExitButtonClass
 * 
 * @author Xihui Chen
 */
public class Opi_relatedDisplayClass extends OpiWidget {

	private static final String typeId = "ActionButton";
	private static final String name = "EDM related display";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_relatedDisplayClass(Context con, Edm_relatedDisplayClass r) {
		super(con, r);
		boolean hookFirstAction=false;
		if (r.getNumDsps() == 1){
			setTypeId(typeId);
			if(r.isInvisible()){
				setTypeId("Rectangle");
				hookFirstAction=true;
				new OpiBoolean(widgetContext, "transparent", true);
				new OpiInt(widgetContext, "line_width", 0);
			}
		} else {
			setTypeId("MenuButton");
			new OpiBoolean(widgetContext, "actions_from_pv", false);
			if(r.isInvisible()){
				new OpiBoolean(widgetContext, "transparent", true);
				new OpiInt(widgetContext, "border_style", 0);
			}
		}
		setName(name);
		setVersion(version);
		
		for (int i = 0; i < r.getNumDsps(); i++) {
			// path
			Element pathNode = widgetContext.getDocument().createElement("path");
			String originPath = r.getDisplayFileName().getEdmAttributesMap().get("" + i).get();
			if (originPath.endsWith(".edl")) {
				originPath = originPath.replace(".edl", ".opi");
			} else
				originPath = originPath + ".opi";
			pathNode.setTextContent(originPath);

			// macros
			Element macrosNode = widgetContext.getDocument().createElement("macros");
			Element includeParentMacroNode = widgetContext.getDocument().createElement(
					"include_parent_macros");
			EdmBoolean isReplaceMacro = r.getReplaceSymbols().getEdmAttributesMap().get("" + i);

			includeParentMacroNode
					.setTextContent(isReplaceMacro != null && isReplaceMacro.is() ? String
							.valueOf(false) : String.valueOf(true));
			macrosNode.appendChild(includeParentMacroNode);
			try {
				EdmString symbols = r.getSymbols().getEdmAttributesMap().get("" + i);
				if (symbols != null) {
					for (String s : StringSplitter.splitIgnoreInQuotes(symbols.get(), ',', true)) {
						String[] rs = StringSplitter.splitIgnoreInQuotes(s, '=', true);
						if (rs.length == 2) {
							try {
								Element m = widgetContext.getDocument().createElement(rs[0]);
								m.setTextContent(rs[1]);
								macrosNode.appendChild(m);
							} catch (Exception e) {
								ErrorHandlerUtil.handleError("Parse Macros Error on: "+s + 
										"(Macro name cannot be number in BOY)", e);
							}
						}
					}
				}
			} catch (Exception e) {
				ErrorHandlerUtil.handleError("Parse Macros Error", e);
			}

			// target
			Element replaceElement = widgetContext.getDocument().createElement("replace");
			EdmBoolean closeDisplay = r.getCloseDisplay().getEdmAttributesMap().get("" + i);
			replaceElement.setTextContent(""
					+ ((closeDisplay != null && closeDisplay.is()) ? 1 : 0));

			// description
			Element descElement = widgetContext.getDocument().createElement("description");
			EdmString menuLabel = r.getMenuLabel().getEdmAttributesMap().get("" + i);
			descElement.setTextContent(menuLabel != null ? menuLabel.get() : "");

			new OpiAction(widgetContext, "OPEN_DISPLAY", Arrays.asList(pathNode, macrosNode,
					replaceElement, descElement), hookFirstAction, false);
		}
		if (r.getButtonLabel() != null) {
			new OpiString(widgetContext, r.getNumDsps() == 1 ? "text" : "label", r.getButtonLabel());
		}

	}

}
