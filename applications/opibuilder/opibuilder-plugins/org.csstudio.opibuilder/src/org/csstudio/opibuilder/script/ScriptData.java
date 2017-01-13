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
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
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

    private ScriptType scriptType;

    private boolean isEmbedded = false;

    private String scriptText;

    private String scriptName;


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
        copy.setEmbedded(isEmbedded);
        copy.setScriptName(scriptName);
        copy.setScriptText(scriptText);
        copy.setScriptType(scriptType);
        for(PVTuple pv : pvList){
            copy.addPV(new PVTuple(pv.pvName, pv.trigger));
        }
        return copy;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if(adapter == IWorkbenchAdapter.class)
            return adapter.cast(new IWorkbenchAdapter() {

                @Override
                public Object getParent(Object o) {
                    return null;
                }

                @Override
                public String getLabel(Object o) {
                    if(isEmbedded)
                        return getScriptName();
                    return path.toString();
                }

                @Override
                public ImageDescriptor getImageDescriptor(Object object) {
                    String icon;
                    if(isEmbedded){
                        if(getScriptType() == ScriptType.PYTHON )
                            icon = "icons/pyEmbedded.gif";
                        else
                            icon = "icons/jsEmbedded.gif";
                    }else if(path != null && !path.isEmpty()
                            && path.getFileExtension().equals(ScriptService.PY)){
                        icon = "icons/python_file.gif"; //$NON-NLS-1$
                    }else
                        icon = "icons/js.gif"; //$NON-NLS-1$
                    return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                            OPIBuilderPlugin.PLUGIN_ID, icon);
                }

                @Override
                public Object[] getChildren(Object o) {
                    return new Object[0];
                }
            });

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

    /**
     * @return the scriptType
     */
    public ScriptType getScriptType() {
        return scriptType;
    }

    /**
     * @param scriptType the scriptType to set
     */
    public void setScriptType(ScriptType scriptType) {
        this.scriptType = scriptType;
    }

    /**
     * @return the isEmbedded
     */
    public boolean isEmbedded() {
        return isEmbedded;
    }

    /**
     * @param isEmbedded the isEmbedded to set
     */
    public void setEmbedded(boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
    }

    /**
     * @return the scriptText
     */
    public String getScriptText() {
        return scriptText;
    }

    /**
     * @param scriptText the scriptText to set
     */
    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    /**
     * @return the scriptName
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @param scriptName the scriptName to set
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }


}
