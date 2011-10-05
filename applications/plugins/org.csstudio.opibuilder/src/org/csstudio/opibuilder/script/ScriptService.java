/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.utility.pv.PV;
import org.eclipse.osgi.util.NLS;

/**The center service for script execution.
 * @author Xihui Chen
 *
 */
public class ScriptService {
	public enum ScriptType{
		JAVASCRIPT("JavaScript"),
		PYTHON("Python/Jython Script");

		private ScriptType(String description) {
			 this.description = description;
		}
		private String description;

		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(ScriptType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}

	public static final String DEFAULT_JS_HEADER = 
			"importPackage(Packages.org.csstudio.opibuilder.scriptUtil);\n"; //$NON-NLS-1$
	public static final String DEFAULT_PYTHONSCRIPT_HEADER = 
			"from org.csstudio.opibuilder.scriptUtil import PVUtil\n"; //$NON-NLS-1$
	
	public static final String PVS = "pvs"; //$NON-NLS-1$

	public static final String WIDGET = "widget"; //$NON-NLS-1$

	public static final String DISPLAY = "display"; //$NON-NLS-1$
	
	public static final String PV_ARRAY_DEPRECIATED = "pvArray"; //$NON-NLS-1$

	public static final String TRIGGER_PV = "triggerPV"; //$NON-NLS-1$
	
	public static final String WIDGET_CONTROLLER_DEPRECIATED = "widgetController"; //$NON-NLS-1$
	
	public static final String JS = "js"; //$NON-NLS-1$
	
	public static final String PY= "py"; //$NON-NLS-1$	
	
	private static ScriptService instance;

	private Map<ScriptData, IScriptStore> scriptMap;

	/** Private constructor to prevent instantiation
	 *  @see #getInstance()
	 */
	private ScriptService(){
		scriptMap = new HashMap<ScriptData, IScriptStore>();		
		
	}

	public synchronized static ScriptService getInstance() {
		if(instance == null)
			instance = new ScriptService();
		return instance;
	}


	/**Register the script in the script service, so that it could be executed afterwards.
	 * @param scriptData
	 * @param editpart
	 * @param pvArray
	 * @throws Exception
	 */
	public void registerScript(final ScriptData scriptData, final AbstractBaseEditPart editpart, final PV[] pvArray){
//		UIBundlingThread.getInstance().addRunnable(new Runnable(){
//			public void run() {
				try {
					scriptMap.put(scriptData, ScriptStoreFactory.getScriptStore(scriptData, editpart, pvArray));
				}catch (Exception e) {
					String name = scriptData instanceof RuleScriptData ?
							((RuleScriptData)scriptData).getRuleData().getName() : scriptData.getPath().toString();
					String errorInfo = NLS.bind("Failed to register {0}. \n{1}",
							name, e);
					ConsoleService.getInstance().writeError(errorInfo);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, errorInfo, e);
				}
//			}
//		});

	}


	public void exit(){
		ScriptStoreFactory.exit();
		instance =  null;
	}

	public void unRegisterScript(ScriptData scriptData){
		if(scriptMap.get(scriptData) != null)
			scriptMap.get(scriptData).unRegister();
		scriptMap.remove(scriptData);
	}

}
