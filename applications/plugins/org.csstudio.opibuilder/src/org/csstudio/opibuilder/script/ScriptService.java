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
import org.csstudio.platform.ui.util.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.eclipse.osgi.util.NLS;
import org.mozilla.javascript.Context;

/**The center service for script execution.
 * @author Xihui Chen
 *
 */
public class ScriptService {

	public static final String PV_ARRAY = "pvArray";

	public static final String WIDGET_CONTROLLER = "widgetController";

	private static ScriptService instance;

	private Context scriptContext;

	private Map<ScriptData, IScriptStore> scriptMap;

	/** Private constructor to prevent instantiation
	 *  @see #getInstance()
	 */
	private ScriptService() {
		scriptMap = new HashMap<ScriptData, IScriptStore>();
		UIBundlingThread.getInstance().addRunnable(new Runnable() {

			public void run() {
				scriptContext = Context.enter();
			}
		});
	}

	public synchronized static ScriptService getInstance() {
		if(instance == null)
			instance = new ScriptService();
		return instance;
	}


	public Context getScriptContext() {
		return scriptContext;
	}

	/**Register the script in the script service, so that it could be executed afterwards.
	 * @param scriptData
	 * @param editpart
	 * @param pvArray
	 * @throws Exception
	 */
	public void registerScript(final ScriptData scriptData, final AbstractBaseEditPart editpart, final PV[] pvArray){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				try {
					scriptMap.put(scriptData, new RhinoScriptStore(scriptData, editpart, pvArray));
				}catch (Exception e) {
					String name = scriptData instanceof RuleScriptData ?
							((RuleScriptData)scriptData).getRuleData().getName() : scriptData.getPath().toString();
					String errorInfo = NLS.bind("Failed to register {0}. \n{1}",
							name, e.getMessage());
					ConsoleService.getInstance().writeError(errorInfo);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, errorInfo, e);
				}
			}
		});

	}


	public void exit(){
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				Context.exit();
			}
		});

		instance =  null;
	}

	public void unRegisterScript(ScriptData scriptData){
		scriptMap.get(scriptData).unRegister();
		scriptMap.remove(scriptData);
	}

}
