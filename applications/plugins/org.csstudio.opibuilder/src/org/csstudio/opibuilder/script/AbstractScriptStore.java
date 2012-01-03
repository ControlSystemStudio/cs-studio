/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * The script store help to store the compiled script for afterward executions.
 * This is the abstract script store implementation for BOY script execution. All script stores
 * in BOY should implement this abstract class with a specific script engine. 
 * The store must be disposed manually when it is not needed.
 * @author Xihui Chen
 *
 */
public abstract class AbstractScriptStore implements IScriptStore{	
	
	private IPath absoluteScriptPath;

	private String errorSource;

	private Map<PV, PVListener> pvListenerMap;

	private boolean errorInScript;

	volatile boolean unRegistered = false;


	/**
	 * A map to see if a PV was triggered before, this is used to skip the first trigger.
	 */
	private Map<PV, Boolean> pvTriggeredMap;

	private boolean triggerSuppressed = false;
	
	private ScriptData scriptData;
	private AbstractBaseEditPart editPart;
	private PV[] pvArray;
	
	public AbstractScriptStore(final ScriptData scriptData, final AbstractBaseEditPart editpart,
			final PV[] pvArray) throws Exception {		
		
		this.scriptData = scriptData;
		this.editPart = editpart;
		this.pvArray = pvArray;
		
		if(!(scriptData instanceof RuleScriptData) && !scriptData.isEmbedded()){			
			absoluteScriptPath = scriptData.getPath();
			if(!absoluteScriptPath.isAbsolute()){
				absoluteScriptPath = ResourceUtil.buildAbsolutePath(
						editpart.getWidgetModel(), absoluteScriptPath);
			}
		}
		
		initScriptEngine();
		
		errorInScript = false;
		errorSource = scriptData instanceof RuleScriptData ?
				((RuleScriptData)scriptData).getRuleData().getName() : scriptData.getPath().toString();

		if(scriptData instanceof RuleScriptData){
			compileString(((RuleScriptData)scriptData).getScriptString());
		}else if(scriptData.isEmbedded())
			compileString(scriptData.getScriptText());
		else{			
			//read file
			InputStream inputStream = ResourceUtil.pathToInputStream(absoluteScriptPath, false);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));

			//compile
			compileReader(reader); //$NON-NLS-1$
			inputStream.close();
			reader.close();
		}		


		pvListenerMap = new HashMap<PV, PVListener>();
		pvTriggeredMap = new HashMap<PV, Boolean>();

		PVListener suppressPVListener = new PVListener() {

			public synchronized void pvValueUpdate(PV pv) {
				if (triggerSuppressed && checkPVsConnected(scriptData, pvArray)) {
					executeScriptInUIThread(pv);
					triggerSuppressed = false;
				}
			}

			public void pvDisconnected(PV pv) {

			}
		};

		PVListener triggerPVListener = new PVListener() {
			public synchronized void pvValueUpdate(PV pv) {

				// skip the first trigger if it is needed.
				if (scriptData.isSkipPVsFirstConnection()
						&& !pvTriggeredMap.get(pv)) {
					pvTriggeredMap.put(pv, true);
					return;
				}

				// execute script only if all input pvs are connected
				if (pvArray.length > 1) {
					if (!checkPVsConnected(scriptData, pvArray)) {
						triggerSuppressed = true;
						return;

					}
				}

				executeScriptInUIThread(pv);
			}

			public void pvDisconnected(PV pv) {
			}
		};
		//register pv listener
		int i=0;
		for(PV pv : pvArray){
			if(pv == null)
				continue;
			if(!scriptData.getPVList().get(i++).trigger){
				//execute the script if it was suppressed.
				pv.addListener(suppressPVListener);
				pvListenerMap.put(pv, suppressPVListener);
				continue;
			};
			pvTriggeredMap.put(pv, false);
			pv.addListener(triggerPVListener);
			pvListenerMap.put(pv, triggerPVListener);

		}
	}

	/**Initialize the script engine.
	 * @param editpart
	 * @param pvArray
	 */
	protected abstract void initScriptEngine() throws Exception ;
	
	/**Compile string with script engine.
	 * @param string
	 * @throws Exception 
	 */
	protected abstract void compileString(String string) throws Exception;

	/**Compile reader with script engine.
	 * @param reader
	 * @throws Exception
	 */
	protected abstract void compileReader(Reader reader) throws Exception;
	
	/**
	 * Execute the script with script engine.
	 * @param triggerPV  the PV that triggers this execution.
	 */
	protected abstract void execScript(final PV triggerPV) throws Exception;
	
	private void executeScriptInUIThread(final PV triggerPV) {
		Display display = editPart.getRoot().getViewer().getControl().getDisplay();
		UIBundlingThread.getInstance().addRunnable(display, new Runnable() {
			public void run() {
				if ((!scriptData.isStopExecuteOnError() || !errorInScript) && !unRegistered) {
					try {
						execScript(triggerPV);
					} catch (Exception e) {
						errorInScript = true;
						final String notExecuteWarning = "\nThe script or rule will not be executed afterwards. " +
								"You can change this setting in script dialog.";
						final String message = NLS
								.bind("Error in {0}.{1}\n{2}",
										new String[]{errorSource, 
										 !scriptData.isStopExecuteOnError()? "" : notExecuteWarning, //$NON-NLS-1$
												 e.toString()});
						ConsoleService.getInstance().writeError(message);
						OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
					}
				}
			}
		});
	}

	private boolean checkPVsConnected(ScriptData scriptData, PV[] pvArray){
		if(!scriptData.isCheckConnectivity())
			return true;
		for(PV pv : pvArray){
			if(!pv.isConnected())
				return false;
		}
		return true;

	}

	public void unRegister() {
		unRegistered = true;
		for(Entry<PV, PVListener> entry :  pvListenerMap.entrySet()){
			entry.getKey().removeListener(entry.getValue());
		}
	}

	/**
	 * @return the scriptData
	 */
	public ScriptData getScriptData() {
		return scriptData;
	}

	/**
	 * @return the editPart
	 */
	public AbstractBaseEditPart getEditPart() {
		return editPart;
	}
	
	/**
	 * @return the display editPart
	 */
	public DisplayEditpart getDisplayEditPart() {		
		if(getEditPart().isActive())
			return (DisplayEditpart)(getEditPart().getViewer().getContents());
		return null;
	}
	

	/**
	 * @return the pvArray
	 */
	public PV[] getPvArray() {
		return pvArray;
	}

	public IPath getAbsoluteScriptPath() {
		return absoluteScriptPath;
	}
	
	
	
}
