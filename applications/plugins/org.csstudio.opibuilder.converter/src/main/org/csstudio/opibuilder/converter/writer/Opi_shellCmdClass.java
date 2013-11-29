/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;

import org.csstudio.opibuilder.converter.model.EdmString;
import org.csstudio.opibuilder.converter.model.Edm_shellCmdClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeExitButtonClass
 * 
 * @author Xihui Chen
 */
public class Opi_shellCmdClass extends OpiWidget {

	private static final String typeId = "ActionButton";
	private static final String name = "EDM shell command";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_shellCmdClass(Context con, Edm_shellCmdClass r) {
		super(con, r);
		if (r.getNumCmds() == 1)
			setTypeId(typeId);
		else {
			setTypeId("MenuButton");
			new OpiBoolean(widgetContext, "actions_from_pv", false);
		}
		setName(name);
		setVersion(version);
		for (int i = 0; i < r.getNumCmds(); i++) {
			//path
			Element cmdNode = widgetContext.getDocument().createElement("command");
			cmdNode.setTextContent(processCommand(r.getCommand().getEdmAttributesMap().get("" + i).get()));

			//command directory
			Element dirNode = widgetContext.getDocument().createElement("command_directory");
			dirNode.setTextContent("$(opi.dir)");

			//description		
			Element descElement = widgetContext.getDocument().createElement("description");
			EdmString menuLabel = r.getCommandLabel().getEdmAttributesMap().get(""+i);
			descElement.setTextContent(menuLabel!=null?menuLabel.get():"");
			
			new OpiAction(widgetContext, "EXECUTE_CMD", Arrays.asList(cmdNode, dirNode, descElement),
					false, false);
		}
		if (r.getButtonLabel() != null){
			new OpiString(widgetContext, r.getNumCmds()==1?"text":"label", r.getButtonLabel());
		}

	}
	
	public static String processCommand(String originCmd){
		if(originCmd.endsWith(" &")){
			originCmd=originCmd.substring(0, originCmd.indexOf(" &"));
		}
		return originCmd;
	}

}
