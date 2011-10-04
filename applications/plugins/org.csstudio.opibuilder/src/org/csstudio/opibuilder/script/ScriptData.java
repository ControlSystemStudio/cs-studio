/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**The description data for a script.
 * @author Xihui Chen
 *
 */
public class ScriptData implements IAdaptable {
	
	public static String SCRIPT_EXTENSION = "js"; //$NON-NLS-1$
	
	/**
	 * The path of the script.
	 */
	private IPath path;
	
	/**
	 * The input PVs of the script. Which can be accessed in the script and trigger the script execution.
	 */
	protected List<PVTuple> pvList;
	
	/**
	 * Check PVs connectivity before executing the script. 
	 */
	private boolean checkConnectivity = true;
	
	/**
	 * Skip the executions triggered by PVs first connection.
	 */
	private boolean skipPVsFirstConnection = false;
	
	/**
	 * Stop to execute the script if error is detected in script.
	 */
	private boolean stopExecuteOnError = false;
	
	
	public ScriptData() {
		path = new Path("");
		pvList = new ArrayList<PVTuple>();
	}
	
	public ScriptData(IPath path) {
		this.path = path;
		pvList = new ArrayList<PVTuple>();
	}
	
	/**Set the script path.
	 * @param path the file path of the script.
	 * @return true if successful. false if the input is not a javascript file.
	 */
	public boolean setPath(IPath path){
		if(path.getFileExtension() != null){
			this.path = path; 
			return true;
		}
		return false;		
	}
	
	/**Get the path of the script.
	 * @return the file path.
	 */
	public IPath getPath() {
		return path;
	}
	
	/**Get the input PVs of the script 
	 * @return
	 */
	public List<PVTuple> getPVList() {
		return pvList;
	}
	
	public void addPV(PVTuple pvTuple){
		if(!pvList.contains(pvTuple)){
			pvList.add(pvTuple);
		}			
	}
	
	public void removePV(PVTuple pvTuple){
		pvList.remove(pvTuple);
	}	
	
	public void setCheckConnectivity(boolean checkConnectivity) {
		this.checkConnectivity = checkConnectivity;
	}

	public boolean isCheckConnectivity() {
		return checkConnectivity;
	}
	
	public ScriptData getCopy(){
		ScriptData copy = new ScriptData();
		copy.setPath(path);
		copy.setCheckConnectivity(checkConnectivity);
		copy.setSkipPVsFirstConnection(skipPVsFirstConnection);
		copy.setStopExecuteOnError(stopExecuteOnError);
		for(PVTuple pv : pvList){
			copy.addPV(new PVTuple(pv.pvName, pv.trigger));
		}
		return copy;
	}


	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if(adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {
				
				public Object getParent(Object o) {
					return null;
				}
				
				public String getLabel(Object o) {
					return path.toString();
				}
				
				public ImageDescriptor getImageDescriptor(Object object) {
					String icon;
					if(path != null && !path.isEmpty() 
							&& path.getFileExtension().equals(ScriptService.PY)){
						icon = "icons/python_file.gif";
					}else
						icon = "icons/js.gif";
					return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
							OPIBuilderPlugin.PLUGIN_ID, icon);
				}
				
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		
		return null;
	}

	/**
	 * @param skipPVsFirstConnection Skip the executions triggered by PVs first connection.
	 */
	public void setSkipPVsFirstConnection(boolean skipPVsFirstConnection) {
		this.skipPVsFirstConnection = skipPVsFirstConnection;
	}

	/**
	 * @return Skip the executions triggered by PVs first connection if it is true.
	 */
	public boolean isSkipPVsFirstConnection() {
		return skipPVsFirstConnection;
	}
	
	/**
	 * @param stopExecuteOnError
	 *  If true, stop to execute the script if error is detected in script.
	 */
	public void setStopExecuteOnError(
			boolean stopExecuteOnError) {
		this.stopExecuteOnError = stopExecuteOnError;
	}
	
	/**
	 * @return true if stop to execute the script if error is detected in script..
	 */
	public boolean isStopExecuteOnError() {
		return stopExecuteOnError;
	}

	
}
