/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XML output class for OPI Rule type.
 * 
 * @author Xihui Chen
 */
public class OpiAction {


	/**Create/add an opi action.
	 * @param widgetContext
	 * @param actionType
	 * @param actionProperties
	 * @param hookfirst
	 * @param hookAll
	 */
	public OpiAction(Context widgetContext, String actionType, List<Element> actionProperties,
			boolean hookfirst, boolean hookAll) {
		if (widgetContext.getElement().getElementsByTagName("actions").getLength() <= 0) {
			Element actionsNode = widgetContext.getDocument().createElement("actions");
			widgetContext.getElement().appendChild(actionsNode);
			actionsNode.setAttribute("hook", String.valueOf(hookfirst));
			actionsNode.setAttribute("hook_all", String.valueOf(hookAll));
		}
		Node actionsNode = widgetContext.getElement().getElementsByTagName("actions").item(0);
		Element actionNode = widgetContext.getDocument().createElement("action");
		actionsNode.appendChild(actionNode);

		actionNode.setAttribute("type", actionType);
		for (Element propElement : actionProperties) {
			actionNode.appendChild(propElement);
		}
	}
}
