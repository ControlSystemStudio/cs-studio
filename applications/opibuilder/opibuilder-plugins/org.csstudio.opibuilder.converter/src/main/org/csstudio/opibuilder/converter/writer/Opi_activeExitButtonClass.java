/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;

import org.csstudio.opibuilder.converter.model.Edm_activeExitButtonClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeExitButtonClass
 * 
 * @author Xihui Chen
 */
public class Opi_activeExitButtonClass extends OpiWidget {

	private static final String typeId = "ActionButton";
	private static final String name = "EDM Exit Button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeExitButtonClass(Context con, Edm_activeExitButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);

		Element embedded = widgetContext.getDocument().createElement("embedded");
		embedded.setTextContent("true");
		Element scriptText = widgetContext.getDocument().createElement("scriptText");
		scriptText.setTextContent("importPackage(Packages.org.csstudio.opibuilder.scriptUtil);"
				+ "ScriptUtil.closeCurrentOPI();");
		new OpiAction(widgetContext, "EXECUTE_JAVASCRIPT", Arrays.asList(embedded, scriptText),
				false, false);
		if (r.getLabel() != null)
			new OpiString(widgetContext, "text", r.getLabel());

	}

}
