/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.util.List;

/**The ScriptData converted from  {@link RuleData}
 * @author Xihui Chen
 *
 */
public class RuleScriptData extends ScriptData {

	private String scriptString;
	private RuleData ruleData;
	
	
	public RuleScriptData(RuleData ruleData) {
		this.ruleData = ruleData;
	}

	public RuleData getRuleData() {
		return ruleData;
	}
	
	/**
	 * @return the scriptString
	 */
	public final String getScriptString() {
		return scriptString;
	}

	/**
	 * @param scriptString the scriptString to set
	 */
	public final void setScriptString(String scriptString) {
		this.scriptString = scriptString;
	}
	
	public void setPVList(List<PVTuple> pvList){
		this.pvList = pvList;
	}
	
	
	
}
